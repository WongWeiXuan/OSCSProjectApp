package login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
	private Service<Void> backgroundService;

	@FXML
	void closeDialog(ActionEvent event) {
		loginDialog.close();
	}

	@FXML
	void openSignup(MouseEvent event) throws IOException {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("SignupPage.fxml"));
		stage.setScene(new Scene(root));
		stage.show();
	}

	@FXML
	void validateLogin(ActionEvent event) throws InterruptedException {
		String username = usernameField.getText();
		String password = passwordField.getText();

		if (!username.isEmpty() && !password.isEmpty()) {
			LoginPageModel login = new LoginPageModel(username, password);
			String complexity = login.checkPasswordComplexity();
			if (complexity.isEmpty()) {
				dialogTitleText.setText("Password complexity checks: Pass");
			} else {
				dialogTitleText.setText("Password complexity checks: " + complexity);
			}

			backgroundService = new Service<Void>() {

				@Override
				protected Task<Void> createTask() {
					return new Task<Void>() {

						@Override
						protected Void call() throws Exception {
							updateMessage("(R.I.P threading) Your hashed password is: " + login.hashPassword());
							return null;
						}
					};
				}
			};

			backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					dialogText.textProperty().unbind();
					loginDialog.show();
					loginBtn.setDisable(false);
				}
			});

			dialogText.textProperty().bind(backgroundService.messageProperty());
			backgroundService.start();
			loginBtn.setDisable(true);
		}
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
	}
}
