package log.controller;

import java.io.IOException;
import java.util.ArrayList;
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
    private Service<Void> backgroundService;
    private static LogModelThread logThread;
    
    @FXML
    void changeLogView(ActionEvent event) {
    	final String LOGNAME = filterCombo.getSelectionModel().getSelectedItem().getText();
    	HandshakeThread.setLogName(LOGNAME); // Setting logName
    	backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() {
						LogModelModel.updateOrGetLogs(LOGNAME);
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
		//LogModelThread.requestFileTransferRequest();
	}

	@FXML
	void filterLogs(ActionEvent event) {

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

	@SuppressWarnings("unchecked")
	@FXML
	void initialize() throws IOException {
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

		ArrayList<String> logList = new Setting().getPreference().getLogFile();
		for(String s : logList) {
			filterCombo.getItems().add(new Label(s));
		}
		filterCombo.getSelectionModel().select(0); //Display whatever is the first log
		
		// Starting the LogThread
		if(logThread == null) {
			logThread = new LogModelThread(logList.get(0));
			
			new Thread(logThread);
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
		
		// Logs
		LogModelModel model = new LogModelModel();
		ObservableList<LogDetailsTree> logDetailsTree = FXCollections.observableArrayList();
		
		// If the log doesnt exist for some reason...
		try {
			ArrayList<LogDetailsTree> logArray = model.getLogsDetails(0);
			logDetailsTree.addAll(logArray);
			
			final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
			logTable.getColumns().setAll(recordNumber, eventType, eventID, eventTimeGenerated, eventData, eventCategory, eventSource, eventTimeWritten);
			logTable.setRoot(root);
			logTable.setShowRoot(false);
			
			filterInput.textProperty().addListener(new ChangeListener<String>() {
				
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					logTable.setPredicate(new Predicate<TreeItem<LogDetailsTree>>() {
						@Override
						public boolean test(TreeItem<LogDetailsTree> t) {
//								String value = filterCombo.getSelectionModel().getSelectedItem().getText();
							Boolean returnValue = t.getValue().getRecordNumber().getValue().contains(newValue);
							return returnValue;
						}
						
					});
				}
			});
		} catch(Exception e) {
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
					ArrayList<LogDetailsTree> logArray = model.getLogsDetails(0);
					logDetailsTree.addAll(logArray);
					
					final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
					logTable.getColumns().setAll(recordNumber, eventType, eventID, eventTimeGenerated, eventData, eventCategory, eventSource, eventTimeWritten);
					logTable.setRoot(root);
					logTable.setShowRoot(false);
					
					filterInput.textProperty().addListener(new ChangeListener<String>() {
						
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
							logTable.setPredicate(new Predicate<TreeItem<LogDetailsTree>>() {
								@Override
								public boolean test(TreeItem<LogDetailsTree> t) {
//										String value = filterCombo.getSelectionModel().getSelectedItem().getText();
									Boolean returnValue = t.getValue().getRecordNumber().getValue().contains(newValue);
									return returnValue;
								}
								
							});
						}
					});
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
}
