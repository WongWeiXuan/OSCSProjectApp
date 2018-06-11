package navigation.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDrawer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class NavigationBarController {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private JFXDrawer jfxDrawer;
	@FXML
	private Label nav1;
	
	@FXML
	void changeIcon(ActionEvent event) {
		
	}

	@FXML
	void changeToWord(ActionEvent event) {

	}

	@FXML
	void closeDrawer(MouseEvent event) {
		jfxDrawer.toggle();
		System.out.println("Clicked");
	}

	@FXML
	void initialize() {
		assert jfxDrawer != null : "fx:id=\"jfxDrawer\" was not injected: check your FXML file 'NavigationBar.fxml'.";

	}
}
