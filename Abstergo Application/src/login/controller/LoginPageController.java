package login.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;

import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.spi.loaderwriter.CacheLoadingException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.events.JFXDialogEvent;

import bluetooth.BluetoothDevice;
import bluetooth.BluetoothThreadModel;
import bluetooth.LoginBluetoothModel;
import javafx.application.Platform;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import login.CacheMan;
import login.LoginPageModel;

public class LoginPageController {
	@FXML
	private AnchorPane root;
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
	public static CacheMan cacheManager;

	private void changeToHome() {
		try {
			AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource("/log/view/LogPage.fxml")); // Change scene
			PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
			for(Node n : PreLoginPageController.stackPaneClone.getChildren()) {
				System.out.println("WDHWUD: " + n.getId());
			}
			PreLoginPageController.stackPaneClone.getChildren().remove(0);
			PreLoginPageController.navBarClone.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void closeDialog(ActionEvent event) {
		loginDialog.close();
	}

	@FXML
	void openSignup(MouseEvent event) throws IOException {
		Parent toBeChanged = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage.fxml")); // Change scene
		PreLoginPageController.stackPaneClone.getChildren().remove(0);
		PreLoginPageController.stackPaneClone.getChildren().add(0, toBeChanged);
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
						protected Boolean call() {
							// updateMessage("Your hashed password is: " +
							// LoginPageModel.byteArrayToHexString(LOGIN.encodeHashPassword()));
							boolean value = false;
							try {
								value = LOGIN.validateAccount();
							} catch (Exception e) {
								e.printStackTrace();
							}
							return value;
						}
					};
				}
			};

			backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					root.setCursor(Cursor.DEFAULT);
					if (backgroundService.getValue()) {
						try {
							if(BluetoothThreadModel.isRunning()) {
								BluetoothThreadModel.stopThread();
							}
							// Checking if bluetoothDevice is in range/on
							BluetoothDevice bd = LoginPageModel.getPairedDevice(username);
							ArrayList<BluetoothDevice> array = new ArrayList<BluetoothDevice>();
							array.add(bd);
							LoginBluetoothModel.setPairedArray(array);

							if (!LoginBluetoothModel.scanForPairedBluetoothDevice()) {
								dialogTitleText.setText("Successful Login BUT...");
								dialogText.setText("Linked device not found.");
								loginDialog.show();
								// Remember to comment/remove this // TODO
								// Store the user logged in and "restore previous session" (if any)
//								if(cacheManager == null) {
//									cacheManager = new CacheMan();
//								}
//								Cache<String, String> userCache = cacheManager.getCacheManager().getCache("user", String.class, String.class);
//								if (userCache == null) {
//									userCache = cacheManager.getCacheManager().createCache("user", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(2)));
//									userCache.put("User", username);
//								}
//								
//								loginDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
//									@Override
//									public void handle(JFXDialogEvent event) {
//										Platform.runLater(new Runnable() {
//											@Override
//											public void run() {
//												changeToHome();
//											}
//										});
//									}
//								});
							} else {
								// Store the user logged in and "restore previous session" (if any)
								if(cacheManager == null) {
									cacheManager = new CacheMan();
								}
								Cache<String, String> userCache = cacheManager.getCacheManager().getCache("user", String.class, String.class);
								if (userCache == null) {
									userCache = cacheManager.getCacheManager().createCache("user", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(2)));
									userCache.put("User", username);
								}
								
								dialogTitleText.setText("Successful Login");
								dialogText.setText("Login Successful.");
								loginDialog.show();
								BluetoothThreadModel.startThread(bd);
								loginDialog.setOnDialogClosed(new EventHandler<JFXDialogEvent>() {
									@Override
									public void handle(JFXDialogEvent event) {
										Platform.runLater(new Runnable() {
											@Override
											public void run() {
												changeToHome();
											}
										});
									}
								});
							}
						} catch (BluetoothStateException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (CacheLoadingException e) {
							e.printStackTrace();
						}
					} else {
						dialogText.setText("Login Failed.");
					}
					// dialogText.textProperty().unbind();
					STAGE.getScene().setCursor(Cursor.DEFAULT);
					loginBtn.setDisable(false);
					loginDialog.show();
				}
			});
			
			root.setCursor(Cursor.WAIT);
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
