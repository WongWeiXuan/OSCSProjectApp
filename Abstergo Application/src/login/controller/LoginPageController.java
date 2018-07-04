package login.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import bluetooth.BluetoothThreadModel;
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
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import login.LoginPageModel;

public class LoginPageController {
	@FXML
	private StackPane loginStackpane;
	@FXML
	private JFXTextField usernameField;
	@FXML
	private JFXPasswordField passwordField;
	@FXML
	private Label signupLabel;
	@FXML
	private JFXButton loginBtn;
	@FXML
	private JFXDialog loginDialog;
	@FXML
	private JFXDialogLayout loginDialogLayout;
	@FXML
	private Label dialogTitleText;
	@FXML
	private Label dialogText;
	@FXML
	private JFXButton dialogCloseBtn;
	private Service<Boolean> backgroundService;
	private static boolean deviceNotFound;

	@FXML
	void closeDialog(ActionEvent event) {
		loginDialog.close();
	}

	@FXML
	void openSignup(MouseEvent event) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage.fxml")); // Change scene
		((Node) event.getSource()).getScene().setRoot(root);
	}

	@FXML
	void checkEnter(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			loginBtn.fire();
		}
	}

	@FXML
	void validateLogin(ActionEvent event) {
		final Stage STAGE = (Stage) ((Node) event.getSource()).getScene().getWindow();
		String username = usernameField.getText();
		String password = passwordField.getText();

		if (!username.isEmpty() && !password.isEmpty()) {
			final LoginPageModel LOGIN = new LoginPageModel(username, password);

			backgroundService = new Service<Boolean>() {

				@Override
				protected Task<Boolean> createTask() {
					return new Task<Boolean>() {

						@Override
						protected Boolean call() throws Exception {
							// updateMessage("Your hashed password is: " +
							// LoginPageModel.byteArrayToHexString(LOGIN.encodeHashPassword()));
							return LOGIN.validateAccount();
						}
					};
				}
			};

			backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				public void handle(WorkerStateEvent event) {
					if (backgroundService.getValue()) {
						dialogText.setText("Login Successful.");
						BluetoothThreadModel.startThread(LoginPageModel.getPairedDevice(username));
					} else {
						dialogText.setText("Login Failed.");
					}
					// dialogText.textProperty().unbind();
					STAGE.getScene().setCursor(Cursor.DEFAULT);
					loginBtn.setDisable(false);
					loginDialog.show();
				}
			});

			// dialogText.textProperty().bind(backgroundService.messageProperty());
			backgroundService.start();
			loginBtn.setDisable(true);
			STAGE.getScene().setCursor(Cursor.WAIT);
		} else {
			dialogText.setText("Enter something, Bro.");
			loginDialog.show();
		}
	}

	public static void setDeviceNotFound(boolean deviceNotFounded) {
		deviceNotFound = deviceNotFounded;
	}

	@FXML
	void initialize() {
		assert loginStackpane != null : "fx:id=\"loginStackpane\" was not injected: check your FXML file 'LoginPage.fxml'.";
		assert usernameField != null : "fx:id=\"usernameField\" was not injected: check your FXML file 'LoginPage.fxml'.";
		assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'LoginPage.fxml'.";
		assert loginBtn != null : "fx:id=\"loginBtn\" was not injected: check your FXML file 'LoginPage.fxml'.";
		assert loginDialog != null : "fx:id=\"loginDialog\" was not injected: check your FXML file 'LoginPage.fxml'.";
		assert loginDialogLayout != null : "fx:id=\"loginDialogLayout\" was not injected: check your FXML file 'LoginPage.fxml'.";
		assert dialogCloseBtn != null : "fx:id=\"dialogCloseBtn\" was not injected: check your FXML file 'LoginPage.fxml'.";

		loginDialog.setDialogContainer(loginStackpane);
		loginDialog.setContent(loginDialogLayout);
		loginDialog.setTransitionType(DialogTransition.CENTER);
		if (deviceNotFound) {
			dialogTitleText.setText("Device not detected");
			dialogText.setText("Please turn on bluetooth on your device or make sure you are not out of range.");
			loginDialog.show();
		}
		deviceNotFound = false;
	}
}
