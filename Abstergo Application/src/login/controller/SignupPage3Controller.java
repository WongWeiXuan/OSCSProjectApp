package login.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;

public class SignupPage3Controller {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private Text emailText;
	@FXML
	private Text deviceText;
	@FXML
	private JFXButton changeBtn;
	@FXML
	private JFXButton finishBtn;

	@FXML
    void goToChange(ActionEvent event) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage.fxml")); // Change scene
		((Node) event.getSource()).getScene().setRoot(root);
    }
	
	@FXML
	void goToLogin(ActionEvent event) throws IOException {
		// Create user to DB
		// TODO
		SignupPageController.cacheManager.removeCache("preConfigured");
		SignupPageController.cacheManager.close();
		
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/LoginPage.fxml")); // Change scene
		((Node) event.getSource()).getScene().setRoot(root);
	}

	@FXML
	void initialize() {
		assert finishBtn != null : "fx:id=\"finishBtn\" was not injected: check your FXML file 'SignupPage3.fxml'.";

		Cache<String, String> cache = SignupPageController.cacheManager.getCache("registration", String.class,
				String.class);
		Cache<String, String> cache2 = SignupPageController.cacheManager.getCache("deviceRegistration", String.class,
				String.class);
		System.out.println(cache.get("Email"));
		System.out.println(cache.get("Password"));
		System.out.println(cache2.get("BluetoothAddress"));
		System.out.println(cache2.get("DeviceName"));
		System.out.println(cache2.get("MajorClass"));
		
		emailText.setText("Email: " + cache.get("Email"));
		deviceText.setText("Device Paired: " + cache2.get("DeviceName"));
	}
}
