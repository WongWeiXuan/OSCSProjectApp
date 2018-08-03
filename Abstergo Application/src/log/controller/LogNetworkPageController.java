package log.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.ehcache.Cache;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import logExtra.BeepThread;
import logExtra.BlockChain;
import logExtra.HandshakeThread;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;

public class LogNetworkPageController {
	@FXML
    private TitledPane titlePane;
	@FXML
    private GridPane gridPane;
	@FXML
	private Rectangle ownRectangle;
	@FXML
	private VBox rightClick;
	@FXML
	private TextFlow rightClickTextFlow;
	@FXML
    private VBox forCopying;
	public static Thread t;
	public static Thread beepThread;
	public static ArrayList<String> listArray;
	private static VBox rightClickClone;
	private static GridPane gridPaneClone;

	public static void beep(String packetAddress) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if(listArray.contains(packetAddress)) {
					ObservableList<Node> childrens = gridPaneClone.getChildren();

				    for (Node node : childrens) {
				    	VBox vbox = (VBox) node;
				        if(((Label) vbox.getChildren().get(0)).getText().equals(packetAddress)) {
				            ((Rectangle) ((StackPane) ((VBox) vbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(0)).setStyle("-fx-fill: #1c202e");
				            break;
				        }
				    }
				}
			}
		});
	}
	
	public static void boop(String packetAddress) {
		if(listArray.contains(packetAddress)) {
			ObservableList<Node> childrens = gridPaneClone.getChildren();

		    for (Node node : childrens) {
		    	VBox vbox = (VBox) node;
		        if(((Label) vbox.getChildren().get(0)).getText().equals(packetAddress)) {
		            ((Rectangle) ((StackPane) ((VBox) vbox.getChildren().get(1)).getChildren().get(0)).getChildren().get(0)).setStyle("-fx-fill: #e8ce0c");
		            break;
		        }
		    }
		}
	}
	// Event Listener on AnchorPane.onMouseClicked
	@FXML
	public void hideRightClick(MouseEvent event) {
		rightClick.setVisible(false);
	}
	
	public static void showRightClick(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			rightClickClone.setLayoutX(event.getScreenX() - 325);
			rightClickClone.setLayoutY(event.getScreenY() - 100);
			rightClickClone.setVisible(true);
        } else {
        	rightClickClone.setVisible(false);
        }
	}
	// Event Listener on TextFlow[#rightClickTextFlow].onMouseClicked
	@FXML
	public void promptConfirm(MouseEvent event) {
		HandshakeThread.sendDelete("192.168.56.2");
	}
	// Event Listener on TextFlow[#rightClickTextFlow].onMouseEntered
	@FXML
	public void changeToBlue(MouseEvent event) {
		rightClickTextFlow.setStyle("-fx-background-color: #3385ff;");
		rightClickTextFlow.getChildren().get(0).setStyle("-fx-fill: white;");
	}
	// Event Listener on TextFlow[#rightClickTextFlow].onMouseExited
	@FXML
	public void changeToBack(MouseEvent event) {
		rightClickTextFlow.setStyle(null);
		rightClickTextFlow.getChildren().get(0).setStyle(null);
	}
	
	public static void deletedCall() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for(int i = 1; i <= gridPaneClone.getChildren().size(); i++) {
					gridPaneClone.getChildren().remove(i);
				}
				
				BlockChain blockchain1 = new BlockChain("Application", "", false);
				ArrayList<String> app = blockchain1.getListOfNetwork();
				BlockChain blockchain2 = new BlockChain("Security", "", false);
				ArrayList<String> sec = blockchain2.getListOfNetwork();
				
				app.removeAll(sec);
				app.addAll(sec);
				
				Collections.sort(app);
				
				listArray = app;
				
				for(int i = 0, j = 1; i < app.size(); i++) {
					if((i + 1) % 3 == 0) {
						j++;
					}
					
					try {
						VBox vbox = new VBox(new Label(app.get(i)), FXMLLoader.load(getClass().getResource("/log/view/forCopying.fxml")));
						gridPaneClone.add(vbox, (((i + 1) % 3) - 1), j);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void setGridPane() throws IOException {
		BlockChain blockchain1 = new BlockChain("Application", "", false);
		ArrayList<String> app = blockchain1.getListOfNetwork();
		BlockChain blockchain2 = new BlockChain("Security", "", false);
		ArrayList<String> sec = blockchain2.getListOfNetwork();
		
		app.removeAll(sec);
		app.addAll(sec);
		
		Collections.sort(app);
		
		listArray = app;
		
		for(int i = 0, j = 1; i < app.size(); i++) {
			if((i + 1) % 3 == 0) {
				j++;
			}
			VBox vbox = new VBox(new Label(app.get(i)), FXMLLoader.load(getClass().getResource("/log/view/forCopying.fxml")));
			
			gridPane.add(vbox, (((i + 1) % 3) - 1), j);
		}
	}
	
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
			userCache.put("Last", "/log/view/LogNetworkPage.fxml");
		}
		
		rightClickClone = rightClick;
		setGridPane();
		
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							titlePane.setExpanded(true);
						}
					});
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				while(true) {
					Random ran = new Random();
					long sleep = ran.nextInt(3900) + 100;
					try {
						Thread.sleep(sleep);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								ownRectangle.setStyle("-fx-fill: #1c202e");
							}
						});
						Thread.sleep(20);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								ownRectangle.setStyle("-fx-fill: #e8ce0c");
							}
						});
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
		
		beepThread = new Thread(new BeepThread());
		beepThread.start();
		
		gridPaneClone = gridPane;
	}
}
