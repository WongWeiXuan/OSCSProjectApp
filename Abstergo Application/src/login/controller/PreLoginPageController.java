package login.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class PreLoginPageController {
	// Navigation bar
    @FXML
    private AnchorPane navBar;
    @FXML
    private JFXDrawer jfxDrawer;
    @FXML
    private VBox drawerVBox;
    @FXML
    private ImageView logoImageview;
    @FXML
    private Label emailNav;
    @FXML
    private Label logsNav;
    @FXML
    private Label settingsNav;
    @FXML
    private VBox anchorPane;
    public static VBox anchorPaneClone;
    public static AnchorPane navBarClone;
    
    @FXML
    void changePage(MouseEvent event) throws IOException {
    	String clicked = ((Label)event.getSource()).getId();
    	AnchorPane toBeChanged = null;
    	if("logsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/log/view/LogPage.fxml")); // Change scene
    	} else if("settingsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/setting/view/SettingPage.fxml")); // Change scene
    	}
    	PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
    }

    @FXML
    void toggleDrawer(MouseEvent event) {
    	jfxDrawer.toggle();
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
