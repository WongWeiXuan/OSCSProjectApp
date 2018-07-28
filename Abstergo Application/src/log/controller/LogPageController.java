package log.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import log.LogDetailsTree;
import log.LogModelModel;
import log.LogModelThread;
import logExtra.HandshakeThread;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;
import setting.Setting;

public class LogPageController {
	// Logs
	@FXML
    private AnchorPane root;
	@FXML
    private StackPane logStackPane;
	@FXML
	private JFXButton updateBtn;
	@FXML
	private JFXButton downloadBtn;
	@FXML
	private JFXButton filterBtn;
	@FXML
	private JFXTreeTableView<LogDetailsTree> logTable;
	@FXML
    private JFXComboBox<Label> filterCombo;
    @FXML
    private JFXTextField filterInput;
    @FXML
    private JFXDialog jfxDialog;
    @FXML
    private JFXDialogLayout jfxDialogLayout;
    @FXML
    private Text popupText;
    private static Text popupTextClone;
    private Service<Void> backgroundService;
    public static LogModelThread logThread;
    public static Thread thread;
    
    /*
     * 4688/592 - New process - Security
     * 4624/528/540 - Account logged in - Security
     * 5140/560 - Share was access
     * 5156 - Firewall network connection by process
     * 7045/601 New service installed - System
     * 4663/567 - File auditing E.g. Files created
     */
   
    @FXML
    void changeLogView(ActionEvent event) {
    	final String LOGNAME = filterCombo.getSelectionModel().getSelectedItem().getText();
    	HandshakeThread.setLogName(LOGNAME); // Setting logName
    	LogModelThread.setLogName(LOGNAME); // Setting logName
    	backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() {
						logThread.updateLogs();
						return null;
					}
				};
			}
		};

		backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				int select = 0;
		    	if("Application".equals(LOGNAME)) {
		    		select = 0;
		    	} else if("System".equals(LOGNAME)) {
		    		select = 1;
		    	} else if("Security".equals(LOGNAME)) {
		    		select = 2;
		    	}
		    	
				LogModelModel model = new LogModelModel();
				ObservableList<LogDetailsTree> logDetailsTree = FXCollections.observableArrayList();
				logDetailsTree.addAll(model.getLogsDetails(select));
				
				final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
				logTable.setRoot(root);
				jfxDialog.close();
			}
		});
		
		jfxDialog.setDialogContainer(logStackPane);
		jfxDialog.setContent(jfxDialogLayout);
		jfxDialog.setTransitionType(DialogTransition.CENTER);
		jfxDialog.show();
		try {
			backgroundService.start();
		} catch(Exception e) {
			jfxDialog.close();
			System.out.println("UNEXPECTED ERROR!");
			System.out.println("PROBABLY NOT ENOUGH PRIVILEDGE");
		}
		
    }
    
	@FXML
	void establishFileTransfer(ActionEvent event) {
		// Just in case something else overwritten the logname
		final String LOGNAME = filterCombo.getSelectionModel().getSelectedItem().getText();
    	HandshakeThread.setLogName(LOGNAME); // Setting logName
    	LogModelThread.setLogName(LOGNAME); // Setting logName
		logThread.requestFileTransferRequest();
	}

	@FXML
	void filterLogs(ActionEvent event) {
		Map<String, Integer> mapping24Hours = new HashMap<String, Integer>();
		Map<String, Integer> mapping1Week = new HashMap<String, Integer>();
		
		LogModelModel model = new LogModelModel();
		Setting setting = new Setting();
		for(String s : setting.getPreference().getLogFile()) {
			Map<String, Integer> map24Hours = null;
			Map<String, Integer> map1Week = null;
			
			if("Application".equals(s)) {
				map24Hours = LogModelModel.processAndGetSummary24Hours(model.getLogsDetailsModel(0), 0); // You have to wonder why I never create a "Model" method -.-
				map1Week = LogModelModel.processAndGetSummary1Week(model.getLogsDetailsModel(0), 0);
			} else if("System".equals(s)) {
				map24Hours = LogModelModel.processAndGetSummary24Hours(model.getLogsDetailsModel(1), 1);
				map1Week = LogModelModel.processAndGetSummary1Week(model.getLogsDetailsModel(1), 1);
			} else if("Security".equals(s)) {
				map24Hours = LogModelModel.processAndGetSummary24Hours(model.getLogsDetailsModel(2), 2);
				map1Week = LogModelModel.processAndGetSummary1Week(model.getLogsDetailsModel(2), 2);
			}
			
			for(Map.Entry<String, Integer> e : map24Hours.entrySet()) {
				mapping24Hours.merge(e.getKey(), e.getValue(), Integer::sum);
			}
			
			for(Map.Entry<String, Integer> e : map1Week.entrySet()) {
				mapping1Week.merge(e.getKey(), e.getValue(), Integer::sum);
			}
		}
		
		for(Entry<String, Integer> i : mapping24Hours.entrySet()) {
			System.out.println("Key: " + i.getKey() + ", Value: " + i.getValue());
		}
		System.out.println("\nWeek");
		for(Entry<String, Integer> i : mapping1Week.entrySet()) {
			System.out.println("Key: " + i.getKey() + ", Value: " + i.getValue());
		}
	}

	@FXML
	void updateAndGetLogs(ActionEvent event) {
		backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() {
						LogModelModel.updateOrGetLogs("Application");
						return null;
					}
				};
			}
		};

		backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				LogModelModel model = new LogModelModel();
				ObservableList<LogDetailsTree> logDetailsTree = FXCollections.observableArrayList();
				logDetailsTree.addAll(model.getLogsDetails(0));
				
				final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
				logTable.setRoot(root);
				jfxDialog.close();
			}
		});
		
		jfxDialog.setDialogContainer(logStackPane);
		jfxDialog.setContent(jfxDialogLayout);
		jfxDialog.setTransitionType(DialogTransition.CENTER);
		jfxDialog.show();
		backgroundService.start();
	}

	public static void setPopupText(String line) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				popupTextClone.setText(line);
			}
		});
	}
	
	public static void setPopupTextDefault() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				popupTextClone.setText("Retrieving logs...");
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	void initialize() throws IOException, InterruptedException {
		popupTextClone = popupText;
		Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
		if (userCache == null) {
			AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource("/login/view/Login.fxml")); // Change scene
			toBeChanged.setOpacity(1);
			PreLoginPageController.stackPaneClone.getChildren().remove(0);
			PreLoginPageController.stackPaneClone.getChildren().add(0, toBeChanged);
			PreLoginPageController.anchorPaneClone.getChildren().clear();
			PreLoginPageController.navBarClone.setVisible(false);
		} else {
			userCache.put("Last", "/setting/view/SettingPage.fxml");
		}
		
		backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() {
						ArrayList<String> logList = new Setting().getPreference().getLogFile();
						for(String s : logList) {
							filterCombo.getItems().add(new Label(s));
						}
						filterCombo.getSelectionModel().select(0); //Display whatever is the first log
						
						final String LOGNAME = logList.get(0);
						// Starting the LogThread
						if(logThread == null) {
							logThread = new LogModelThread(LOGNAME);
							
							thread = new Thread(logThread);
							thread.start();
						}
						
						JFXTreeTableColumn<LogDetailsTree, String> eventCategory = new JFXTreeTableColumn<LogDetailsTree, String>("Event Category");
						eventCategory.setPrefWidth(150);
						eventCategory.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventCategory();
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> eventID = new JFXTreeTableColumn<LogDetailsTree, String>("Event ID");
						eventID.setPrefWidth(150);
						eventID.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventID();
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> eventSource = new JFXTreeTableColumn<LogDetailsTree, String>("Event Source");
						eventSource.setPrefWidth(150);
						eventSource.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventSource();
							}
							
						});

						JFXTreeTableColumn<LogDetailsTree, String> eventType = new JFXTreeTableColumn<LogDetailsTree, String>("Event Type");
						eventType.setPrefWidth(150);
						eventType.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventType();
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> recordNumber = new JFXTreeTableColumn<LogDetailsTree, String>("Record Number");
						recordNumber.setPrefWidth(150);
						recordNumber.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getRecordNumber();
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> eventTimeGenerated = new JFXTreeTableColumn<LogDetailsTree, String>("Event Time Generated");
						eventTimeGenerated.setPrefWidth(150);
						eventTimeGenerated.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventTimeGenerated();
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> eventTimeWritten = new JFXTreeTableColumn<LogDetailsTree, String>("Event Time Written");
						eventTimeWritten.setPrefWidth(150);
						eventTimeWritten.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventTimeWritten();
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> eventData = new JFXTreeTableColumn<LogDetailsTree, String>("Event Data");
						eventData.setPrefWidth(150);
						eventData.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								return param.getValue().getValue().getEventData();
							}
							
						});
						
						int select = 0;
				    	if("Application".equals(LOGNAME)) {
				    		select = 0;
				    	} else if("System".equals(LOGNAME)) {
				    		select = 1;
				    	} else if("Security".equals(LOGNAME)) {
				    		select = 2;
				    	}
						LogModelModel model = new LogModelModel();
						ObservableList<LogDetailsTree> logDetailsTree = FXCollections.observableArrayList();
						ArrayList<LogDetailsTree> logArray = model.getLogsDetails(select);
						logDetailsTree.addAll(logArray);
						
						final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								logTable.getColumns().setAll(recordNumber, eventType, eventID, eventTimeGenerated, eventData, eventCategory, eventSource, eventTimeWritten);
								logTable.setRoot(root);
								logTable.setShowRoot(false);
							}
						});
						
						filterInput.textProperty().addListener(new ChangeListener<String>() {
							
							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
								logTable.setPredicate(new Predicate<TreeItem<LogDetailsTree>>() {
									@Override
									public boolean test(TreeItem<LogDetailsTree> t) {
										Boolean returnValue = t.getValue().getEventID().getValue().substring(0, t.getValue().getEventID().getValue().length() - 1).contains(newValue);
										return returnValue;
									}
									
								});
							}
						});
						
						return null;
					}
				};
			}
		};

		backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				jfxDialog.close();
			}
		});
		
		backgroundService.start();
		jfxDialog.setDialogContainer(logStackPane);
		jfxDialog.setContent(jfxDialogLayout);
		jfxDialog.setTransitionType(DialogTransition.CENTER);
		jfxDialog.show();
	}
}
