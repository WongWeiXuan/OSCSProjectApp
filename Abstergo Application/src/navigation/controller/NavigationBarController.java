package navigation.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;

import bluetooth.BluetoothThreadModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import log.LogModelThread;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;
import login.controller.SignupPageController;

public class NavigationBarController {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
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
    private Label logoutNav;
    private boolean toggle = false;
    
    @FXML
	void changePage(MouseEvent event) throws IOException {
		String clicked = ((Label)event.getSource()).getId();
    	AnchorPane toBeChanged = null;
    	if("logsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/log/view/LogPage.fxml")); // Change scene
    		PreLoginPageController.stackPaneClone.getChildren().setAll(toBeChanged);
    	} else if("settingsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/setting/view/SettingPage.fxml")); // Change scene
    		PreLoginPageController.stackPaneClone.getChildren().setAll(toBeChanged);
    	} else if("logoutNav".equals(clicked)) {
    		// Stopping some Threads
    		if(BluetoothThreadModel.isRunning())
    			BluetoothThreadModel.stopThread();
    		if(LogModelThread.isRunning())
	    		LogModelThread.stop();
    		
    		// Removing Cache
    		LoginPageController.cacheManager.getCacheManager().removeCache("user");
    		
    		// Changing Scene
    		toBeChanged = FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml")); // Change scene
    		toBeChanged.setOpacity(1);
    		PreLoginPageController.stackPaneClone.getChildren().add(0, toBeChanged);
    		PreLoginPageController.navBarClone.setVisible(false);
    	}
	}
    
	@FXML
	void toggleDrawer(MouseEvent event) {
		jfxDrawer.toggle();
		if(toggle) {
			System.out.println("WADWAIDHWAID");
			VBox.setMargin(PreLoginPageController.anchorPaneClone, new Insets(0, 120, 0, 0));
			toggle = false;
		} else {
			System.out.println("WADWAtrueIDHWAID");
			VBox.setMargin(PreLoginPageController.anchorPaneClone, new Insets(0, 252, 0, 0));
			toggle = true;
		}
	}

	@FXML
	void initialize() {
		// Navigation
		jfxDrawer.setSidePane(drawerVBox);
		jfxDrawer.setDefaultDrawerSize(120);
		jfxDrawer.setOverLayVisible(true);
		jfxDrawer.setDirection(DrawerDirection.RIGHT);
		jfxDrawer.close();
		
		// Others
	}
}
