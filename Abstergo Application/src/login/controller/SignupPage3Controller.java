package login.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class SignupPage3Controller {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private JFXButton finishBtn;

	@FXML
	void goToLogin(ActionEvent event) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/LoginPage.fxml")); //Change scene
		((Node) event.getSource()).getScene().setRoot(root);
	}

	@FXML
	void initialize() {
		assert finishBtn != null : "fx:id=\"finishBtn\" was not injected: check your FXML file 'SignupPage3.fxml'.";

	}
}
