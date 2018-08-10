package login.controller;

import java.io.IOException;

import javax.naming.OperationNotSupportedException;

import org.ehcache.Cache;

import com.google.gson.JsonArray;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;

import bluetooth.BluetoothThreadModel;
import email.controller.EmailDAO;
import email.controller.services.CreateAndRegisterEmailAccountService;
import email.controller.services.FolderUpdaterService;
import email.view.ViewFactory;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import log.LogModelThread;
import log.controller.LogPageController;
import logExtra.BeepThread;
import logExtra.HandshakeThread;

public class PreLoginPageController {
	public PreLoginPageController() {
		
	}
	
	// Navigation bar
	@FXML
	private StackPane navBar;
	@FXML
	private JFXDrawer jfxDrawer;
	@FXML
	private VBox drawerVBox;
	@FXML
	private ImageView logoImageview;
	@FXML
	private Label emailNav;
	@FXML
	private Label fileNav;
	@FXML
	private Label backupNav;
	@FXML
	private Label logsNav;
	@FXML
	private Label settingsNav;
	@FXML
	private Label logoutNav;
	@FXML
	private VBox anchorPane;
	private boolean toggle = false;
	public static VBox anchorPaneClone;
	public static StackPane navBarClone;

	@FXML
	public void nextPage() throws OperationNotSupportedException {
		AnchorPane toBeChangedd = null;
		toBeChangedd = (AnchorPane) ViewFactory.defaultFactory.getMainScene(); // Change scene
		anchorPaneClone.getChildren().setAll(toBeChangedd);
	}
	
	@FXML
	void changePage(MouseEvent event) throws IOException, OperationNotSupportedException {
		// Stop beep after changing view
		if(BeepThread.isRunning())
			BeepThread.stop();

		String clicked = ((Label)event.getSource()).getId();
    	AnchorPane toBeChanged = null;
    	if("emailNav".equals(clicked)) {
    		Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
    	   	String username = userCache.get("User");
    	   	JsonArray json = EmailDAO.getdetails(username);
    	   	if(json.isJsonNull() || json.size() == 0) {
    	   		Parent parent = ViewFactory.defaultFactory.getAddAccountScene();
    	    	Stage stage = new Stage();
    			JFXDecorator decorator = new JFXDecorator(stage, parent);
    			decorator.setCustomMaximize(true);
    			Scene scene = new Scene(decorator);
    	    	stage.setScene(scene);
    	    	stage.show();
	    		System.out.println("nothing");
    	   	}
    	   	else {
	    		toBeChanged = (AnchorPane) ViewFactory.defaultFactory.getMainScene(); // Change scene
	    		anchorPane.getChildren().setAll(toBeChanged);
	    		System.out.println("something");
    	   	}
		  } else if("fileNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/fileStorage/FileStorage.fxml")); // Change scene
    		anchorPane.getChildren().setAll(toBeChanged);
    	} else if("backupNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/fileBackup/FileBackupBegin.fxml")); // Change scene
    		anchorPane.getChildren().setAll(toBeChanged);
    	} else if("logsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/log/view/LogPage.fxml")); // Change scene
    		anchorPane.getChildren().setAll(toBeChanged);
    	} else if("settingsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/setting/view/SettingPage.fxml")); // Change scene
    		anchorPane.getChildren().setAll(toBeChanged);
    	} else if("logoutNav".equals(clicked)) {
    		// Stopping some Threads
    		if(BluetoothThreadModel.isRunning())
    			BluetoothThreadModel.stopThread();
    		if(LogModelThread.isRunning()) {
    			LogModelThread.stop();
    			LogPageController.thread.stop();
    			LogPageController.logThread = null;
    			// Unlocking locks just in case
    			LogModelThread.lock.unlock();
    			HandshakeThread.lock.unlock();
    			HandshakeThread.lock2.unlock();
    		}
    		
    		//xz part
	    	if(FolderUpdaterService.isitRunning()) {
	    		FolderUpdaterService.stop();
	    	}
    		
    		// Removing Cache
    		LoginPageController.cacheManager.getCacheManager().removeCache("user");
    		
    		// Changing Scene
    		anchorPane.getChildren().clear();
    		toBeChanged = FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml")); // Change scene
    		toBeChanged.setOpacity(1);
			stackPane.getChildren().add(0, toBeChanged);
			navBar.setVisible(false);
		}
	}

	@FXML
	void toggleDrawer(MouseEvent event) {
		jfxDrawer.toggle();
		if (toggle) {
			StackPane.setMargin(anchorPane, new Insets(0, 102, 0, 0));
			toggle = false;
		} else {
			StackPane.setMargin(anchorPane, new Insets(0, 252, 0, 0));
			toggle = true;
		}
	}

	// Page
	@FXML
	private StackPane stackPane;
	private static FadeTransition fadeOut;
	private static FadeTransition fadeIn2;
	public static StackPane stackPaneClone;

	public static void playRest() {
		fadeOut.play();
		fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stackPaneClone.getChildren().remove(1);
				fadeIn2.play();
			}

		});
	}

	@FXML
	void initialize() throws IOException {
		// NavBar
		jfxDrawer.setSidePane(drawerVBox);
		jfxDrawer.setDefaultDrawerSize(120);
		jfxDrawer.setOverLayVisible(true);
		jfxDrawer.setDirection(DrawerDirection.RIGHT);
		jfxDrawer.close();

		// Page
		stackPaneClone = stackPane;
		anchorPaneClone = anchorPane;
		navBarClone = navBar;
		AnchorPane vali = FXMLLoader.load(getClass().getResource("../../validation/view/ValidationPage.fxml"));
		AnchorPane login = FXMLLoader.load(getClass().getResource("../../login/view/LoginPage.fxml"));
		stackPane.getChildren().add(0, vali);
		stackPane.getChildren().add(0, login);

		FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vali);
		fadeIn.setCycleCount(1);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);

		fadeOut = new FadeTransition(Duration.seconds(1), vali);
		fadeOut.setCycleCount(1);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);

		fadeIn2 = new FadeTransition(Duration.seconds(1), login);
		fadeIn2.setCycleCount(1);
		fadeIn2.setFromValue(0);
		fadeIn2.setToValue(1);

		fadeIn.play();
	}
}
