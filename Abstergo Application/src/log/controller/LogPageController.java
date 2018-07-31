package log.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.input.MouseEvent;
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
    private JFXButton infoBtn;
	@FXML
    private Text newProc24;
    @FXML
    private Text accLoged24;
    @FXML
    private Text shareFol24;
    @FXML
    private Text connectionPro24;
    @FXML
    private Text newServ24;
    @FXML
    private Text audit24;
    @FXML
    private Text newProc7;
    @FXML
    private Text accLoged7;
    @FXML
    private Text shareFol7;
    @FXML
    private Text connectionPro7;
    @FXML
    private Text newServ7;
    @FXML
    private Text audit7;
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
						processLogs();
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
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						logTable.setRoot(root);
					}
				});
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
    void changeToInfo(ActionEvent event) throws IOException {
    	AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource("/log/view/LogNetworkPage.fxml")); // Change scene
		PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
    }
    
	@FXML
    void showEventIDLogs(MouseEvent event) {
		backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() {
						final String NODEID = ((Text) event.getSource()).getId();
						int select = 0;
						if("newProc24".equals(NODEID)) {
							select = 2;
						} else if("accLoged24".equals(NODEID)) {
							select = 2;
						} else if("shareFol24".equals(NODEID)) {
							select = 1;
						}else if("connectionPro24".equals(NODEID)) {
							select = 1;
						}else if("newServ24".equals(NODEID)) {
							select = 1;
						}else if("audit24".equals(NODEID)) {
							select = 1;
						}else if("newProc7".equals(NODEID)) {
							select = 2;
						}else if("accLoged7".equals(NODEID)) {
							select = 2;
						}else if("shareFol7".equals(NODEID)) {
							select = 1;
						}else if("connectionPro7".equals(NODEID)) {
							select = 1;
						}else if("newServ7".equals(NODEID)) {
							select = 1;
						}else if("audit7".equals(NODEID)) {
							select = 1;
						}
						
						if(filterCombo.getSelectionModel().getSelectedIndex() != select) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									int select = 0;
									if("newProc24".equals(NODEID)) {
										select = 2;
									} else if("accLoged24".equals(NODEID)) {
										select = 2;
									} else if("shareFol24".equals(NODEID)) {
										select = 1;
									}else if("connectionPro24".equals(NODEID)) {
										select = 1;
									}else if("newServ24".equals(NODEID)) {
										select = 1;
									}else if("audit24".equals(NODEID)) {
										select = 1;
									}else if("newProc7".equals(NODEID)) {
										select = 2;
									}else if("accLoged7".equals(NODEID)) {
										select = 2;
									}else if("shareFol7".equals(NODEID)) {
										select = 1;
									}else if("connectionPro7".equals(NODEID)) {
										select = 1;
									}else if("newServ7".equals(NODEID)) {
										select = 1;
									}else if("audit7".equals(NODEID)) {
										select = 1;
									}
									
									String logName = "";
									if(1 == select) {
										logName = "Application";
									} else if(2 == select) {
										logName = "Security";
									} else if(3 == select) {
										logName = "System";
									}
									
									for(int i = 0; i < filterCombo.getItems().size(); i++) {
										if(filterCombo.getItems().get(i).getText().equals(logName)) {
											filterCombo.getSelectionModel().select(i);
										}
									}
								}
							});
							LogModelModel model = new LogModelModel();
							ObservableList<LogDetailsTree> logDetailsTree = FXCollections.observableArrayList();
							logDetailsTree.addAll(model.getLogsDetails(select));
							
							final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									logTable.setRoot(root);
								}
							});
						}
						
						logTable.setPredicate(new Predicate<TreeItem<LogDetailsTree>>() {
							@Override
							public boolean test(TreeItem<LogDetailsTree> t) {
								ArrayList<String> eventToBe = new ArrayList<String>();
								long dayToBe = 0;
								
								if("newProc24".equals(NODEID)) {
									eventToBe.add("4688");
									eventToBe.add("592");
									dayToBe = 86400000;
								} else if("accLoged24".equals(NODEID)) {
									eventToBe.add("4624");
									eventToBe.add("528");
									eventToBe.add("540");
									dayToBe = 86400000;
								} else if("shareFol24".equals(NODEID)) {
									eventToBe.add("5140");
									eventToBe.add("560");
									dayToBe = 86400000;
								}else if("connectionPro24".equals(NODEID)) {
									eventToBe.add("5156");
									dayToBe = 86400000;
								}else if("newServ24".equals(NODEID)) {
									eventToBe.add("7045");
									eventToBe.add("601");
									dayToBe = 86400000;
								}else if("audit24".equals(NODEID)) {
									eventToBe.add("4663");
									eventToBe.add("567");
									dayToBe = 86400000;
								}else if("newProc7".equals(NODEID)) {
									eventToBe.add("4688");
									eventToBe.add("592");
									dayToBe = 604800000;
								}else if("accLoged7".equals(NODEID)) {
									eventToBe.add("4624");
									eventToBe.add("528");
									eventToBe.add("540");
									dayToBe = 604800000;
								}else if("shareFol7".equals(NODEID)) {
									eventToBe.add("5140");
									eventToBe.add("560");
									dayToBe = 604800000;
								}else if("connectionPro7".equals(NODEID)) {
									eventToBe.add("5156");
									dayToBe = 604800000;
								}else if("newServ7".equals(NODEID)) {
									eventToBe.add("7045");
									eventToBe.add("601");
									dayToBe = 604800000;
								}else if("audit7".equals(NODEID)) {
									eventToBe.add("4663");
									eventToBe.add("567");
									dayToBe = 604800000;
								}
								long time = (System.currentTimeMillis() - dayToBe) / 1000;
								
								String eventID = t.getValue().getEventID().getValue().substring(0, t.getValue().getEventID().getValue().length() - 1);
								Boolean a = false;
								for(String s : eventToBe) {
									if(eventID.equals(s)) {
										a = true;
									}
								}
								String timeA = t.getValue().getEventTimeGenerated().getValue();
								timeA = timeA.substring(0, timeA.length() - 1);
								Boolean b = Long.parseLong(timeA) > time;
								
								if(a && b) {
									return true;
								} else {
									return false;
								}
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
		jfxDialog.setDialogContainer(logStackPane);
		jfxDialog.setContent(jfxDialogLayout);
		jfxDialog.setTransitionType(DialogTransition.CENTER);
		jfxDialog.show();
		backgroundService.start();
    }

	@FXML
	void updateAndGetLogs(ActionEvent event) {
		backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() {
						logThread.updateLogs();
						processLogs();
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
	
	private void processLogs() {
		// Process and show logs info / summary
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
			String key = i.getKey();
			int value = i.getValue();
			Text text = getIDFromKey(key, true);
			text.setText(String.valueOf(value));
			if("logged".equals(key) && value > 0) {
				text.setStyle("-fx-fill: #84E7D8;");
			}else {
				if(value >= 5) {
					text.setStyle("-fx-fill: #FF5D53;");
				}else if(value >= 2) {
					text.setStyle("-fx-fill: #E8DA4D;");
				}else {
					text.setStyle("-fx-fill: black;");
				}
			}
		}
		
		for(Entry<String, Integer> i : mapping1Week.entrySet()) {
			String key = i.getKey();
			int value = i.getValue();
			Text text = getIDFromKey(key, false);
			text.setText(String.valueOf(value));
			if("logged".equals(key) && value > 0) {
				text.setStyle("-fx-fill: #84E7D8;");
			}else {
				if(value >= 5) {
					text.setStyle("-fx-fill: #FF5D53;");
				}else if(value >= 2) {
					text.setStyle("-fx-fill: #E8DA4D;");
				}else {
					text.setStyle("-fx-fill: black;");
				}
			}
		}
	}
	
	private Text getIDFromKey(String key, boolean isIt24) {
		if(isIt24) {
			if("process".equals(key)) {
				return newProc24;
			} else if("logged".equals(key)) {
				return accLoged24;
			} else if("share".equals(key)) {
				return shareFol24;
			}else if("connection".equals(key)) {
				return connectionPro24;
			}else if("service".equals(key)) {
				return newServ24;
			}else if("file".equals(key)) {
				return audit24;
			}
		} else {
			if("process".equals(key)) {
				return newProc7;
			}else if("logged".equals(key)) {
				return accLoged7;
			}else if("share".equals(key)) {
				return shareFol7;
			}else if("connection".equals(key)) {
				return connectionPro7;
			}else if("service".equals(key)) {
				return newServ7;
			}else if("file".equals(key)) {
				return audit7;
			}
		}
		return null;
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
						
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								filterCombo.getSelectionModel().select(0); //Display whatever is the first log
							}
						});
						
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
								String timeGen = param.getValue().getValue().getEventTimeGenerated().getValue();
								timeGen = timeGen.substring(0, timeGen.length() - 1);
								Date date = new Date(Long.parseLong(timeGen) * 1000);
								String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
								return new SimpleStringProperty(time);
							}
							
						});
						
						JFXTreeTableColumn<LogDetailsTree, String> eventTimeWritten = new JFXTreeTableColumn<LogDetailsTree, String>("Event Time Written");
						eventTimeWritten.setPrefWidth(150);
						eventTimeWritten.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

							@Override
							public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
								String timeGen = param.getValue().getValue().getEventTimeWritten().getValue();
								timeGen = timeGen.substring(0, timeGen.length() - 1);
								Date date = new Date(Long.parseLong(timeGen) * 1000);
								String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
								return new SimpleStringProperty(time);
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
										Boolean a = t.getValue().getRecordNumber().getValue().contains(newValue);
										Boolean b = t.getValue().getEventType().getValue().contains(newValue);
										Boolean c = t.getValue().getEventTimeGenerated().getValue().contains(newValue);
										Boolean d = t.getValue().getEventData().getValue().contains(newValue);
										Boolean e = t.getValue().getEventCategory().getValue().contains(newValue);
										Boolean f = t.getValue().getEventSource().getValue().contains(newValue);
										Boolean g = t.getValue().getEventTimeWritten().getValue().contains(newValue);
										Boolean i = t.getValue().getEventID().getValue().contains(newValue);
										
										if(a || b || c || d || e || f || g || i) {
											return true;
										} else {
											return false;
										}
									}
									
								});
							}
						});
						processLogs();
						
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
