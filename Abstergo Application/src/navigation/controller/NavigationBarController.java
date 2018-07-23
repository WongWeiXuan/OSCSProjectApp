package navigation.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import login.controller.PreLoginPageController;

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
    private boolean toggle = false;
    
    @FXML
    void changePage(MouseEvent event) throws IOException {
    	String clicked = ((Label)event.getSource()).getId();
    	AnchorPane toBeChanged = null;
    	if("logsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/log/view/LogPage.fxml")); // Change scene
    	} else if("settingsNav".equals(clicked)) {
    		toBeChanged = FXMLLoader.load(getClass().getResource("/setting/view/SettingPage.fxml")); // Change scene
    	}
    	PreLoginPageController.stackPaneClone.getChildren().setAll(toBeChanged);
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
