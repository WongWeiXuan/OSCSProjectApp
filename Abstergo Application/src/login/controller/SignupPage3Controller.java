package login.controller;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;

import bluetooth.BluetoothDevice;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import login.LoginPageModel;

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
	private Cache<String, String> cache;
	private Cache<String, String> cache2;
	private Service<Void> backgroundService;

	@FXML
	void goToChange(ActionEvent event) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage.fxml")); // Change scene
		((Node) event.getSource()).getScene().setRoot(root);
	}

	@FXML
	void goToLogin(ActionEvent event) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		final Stage STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();

		backgroundService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						// Create user to DB
						BluetoothDevice device = new BluetoothDevice(cache2.get("BluetoothAddress"),
								cache2.get("DeviceName"), cache2.get("MajorClass"));
						LoginPageModel login = new LoginPageModel(cache.get("Email"), cache.get("Password"));
						login.setPassword(LoginPageModel.byteArrayToHexString(login.encodeHashPassword()));
						BluetoothDevice.createLogin(login, device);
						return null;
					}
				};
			}
		};

		backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			public void handle(WorkerStateEvent event) {
				try {
					STAGE.getScene().setCursor(Cursor.DEFAULT);
					SignupPageController.cacheManager.getCacheManager().removeCache("preConfigured");
					SignupPageController.cacheManager.getCacheManager().close();

					Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/LoginPage.fxml"));
					// Change scene
					STAGE.getScene().setRoot(root);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		STAGE.getScene().setCursor(Cursor.WAIT);
		backgroundService.start();
	}

	@FXML
	void initialize() {
		assert finishBtn != null : "fx:id=\"finishBtn\" was not injected: check your FXML file 'SignupPage3.fxml'.";

		cache = SignupPageController.cacheManager.getCacheManager().getCache("registration", String.class,
				String.class);
		cache2 = SignupPageController.cacheManager.getCacheManager().getCache("deviceRegistration", String.class,
				String.class);

		emailText.setText("Email: " + cache.get("Email"));
		deviceText.setText("Device Paired: " + cache2.get("DeviceName"));
	}
}
