package login.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import login.CacheMan;
import login.LoginPageModel;

public class SignupPageController {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private Label loginText;
	@FXML
	private TextFlow alreadyExistLabel;
	@FXML
	private JFXTextField emailField;
	@FXML
	private ImageView emailX;
	@FXML
	private TextFlow passwordCheckTextFlow;
	@FXML
	private Label passwordCheckTextLabel;
	@FXML
	private JFXPasswordField passwordField;
	@FXML
	private ImageView passwordX;
	@FXML
	private JFXPasswordField passwordRetypeField;
	@FXML
	private ImageView passwordRetypeX;
	@FXML
	private JFXButton nextBtn;
	private Boolean[] checks = { false, false, false };
	private Service<Boolean> backgroundService;
	static CacheMan cacheManager;

	private void checkPassword() {
		String password = passwordField.getText();
		String retypedPassword = passwordRetypeField.getText();

		if (!password.isEmpty()) {
			if (password.equals(retypedPassword)) {
				passwordRetypeX.setVisible(false);
				checks[2] = true;
			} else {
				passwordRetypeX.setVisible(true);
				checks[2] = false;
			}
		} else {
			passwordRetypeX.setVisible(true);
			checks[2] = false;
		}
	}

	private void checkPasswordComplexity() {
		String password = passwordField.getText();

		if (password.isEmpty()) {
			passwordCheckTextFlow.setVisible(false);
			passwordX.setVisible(true);
			checks[1] = false;
		} else {
			String returnText = LoginPageModel.checkPasswordComplexity(password);
			if (returnText.equals("Pass")) {
				passwordCheckTextLabel.setStyle("-fx-text-fill: green");
				passwordX.setVisible(false);
				checks[1] = true;
			} else {
				passwordCheckTextLabel.setStyle("-fx-text-fill: red");
				passwordX.setVisible(true);
				checks[1] = false;
			}

			passwordCheckTextLabel.setText(returnText);
			passwordCheckTextFlow.setVisible(true);
		}
		checkPassword();
	}

	void checkEmail() {
		String email = emailField.getText();
		if (email != null && !email.isEmpty()) {
			backgroundService = new Service<Boolean>() {
				@Override
				protected Task<Boolean> createTask() {
					return new Task<Boolean>() {

						@Override
						protected Boolean call() throws Exception {
							return LoginPageModel.checkWhetherEmailExist(email);
						}
					};
				}
			};

			backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				public void handle(WorkerStateEvent event) {
					if (backgroundService.getValue()) {
						alreadyExistLabel.setVisible(true);
						emailX.setVisible(true);
						checks[0] = false;
					} else {
						if (alreadyExistLabel.isVisible() && emailX.isVisible()) {
							alreadyExistLabel.setVisible(false);
							emailX.setVisible(false);
						}
						checks[0] = true;
					}
				}
			});

			backgroundService.start();
		}
	}

	@FXML
	void checkEnter(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			nextBtn.fire();
		}
	}

	@FXML
	void goToLogin(MouseEvent event) {
		try {
			Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/LoginPage.fxml"));
			((Node) event.getSource()).getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void nextPage(ActionEvent event) throws IOException {
		if (emailField.validate() && passwordField.validate() && passwordRetypeField.validate() && checks[0]
				&& checks[1] && checks[2]) {
			extracted();
			Cache<String, String> registration = cacheManager.getCacheManager().getCache("registration", String.class,
					String.class);
			if (registration == null) {
				registration = cacheManager.getCacheManager().createCache("registration", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(2)));
				registration.put("Email", emailField.getText());
				registration.put("Password", passwordField.getText());
			} else {
				registration.replace("Email", emailField.getText());
				registration.replace("Password", passwordField.getText());
			}

			try {
				Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage2.fxml"));
				((Node) event.getSource()).getScene().setRoot(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void extracted() {
		cacheManager = new CacheMan();
	}

	@FXML
	void initialize() {
		assert emailField != null : "fx:id=\"emailField\" was not injected: check your FXML file 'SignupPage.fxml'.";
		assert passwordCheckTextFlow != null : "fx:id=\"passwordCheckTextFlow\" was not injected: check your FXML file 'SignupPage.fxml'.";
		assert passwordCheckTextLabel != null : "fx:id=\"passwordCheckTextLabel\" was not injected: check your FXML file 'SignupPage.fxml'.";
		assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'SignupPage.fxml'.";
		assert passwordRetypeField != null : "fx:id=\"passwordRetypeField\" was not injected: check your FXML file 'SignupPage.fxml'.";
		assert nextBtn != null : "fx:id=\"nextBtn\" was not injected: check your FXML file 'SignupPage.fxml'.";

		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage("Please fill in the field...");
		RequiredFieldValidator validator2 = new RequiredFieldValidator();
		validator2.setMessage("Please fill in the field...");
		RequiredFieldValidator validator3 = new RequiredFieldValidator();
		validator3.setMessage("Please fill in the field...");
		emailField.getValidators().add(validator);
		passwordField.getValidators().add(validator2);
		passwordRetypeField.getValidators().add(validator3);

		emailField.textProperty().addListener(new ChangeListener<Object>() {

			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				emailField.validate();
				checkEmail();
				checks[0] = false;
			}
		});

		passwordField.textProperty().addListener(new ChangeListener<Object>() {

			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				passwordField.validate();
				checkPasswordComplexity();
			}
		});

		passwordRetypeField.textProperty().addListener(new ChangeListener<Object>() {

			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				passwordRetypeField.validate();
				checkPassword();
			}
		});

		if (cacheManager != null) {
			Cache<String, String> cache = cacheManager.getCacheManager().getCache("registration", String.class,
					String.class);
			emailField.setText(cache.get("Email"));
			passwordField.setText(cache.get("Password"));
			passwordRetypeField.setText(cache.get("Password"));
		}
	}
}
