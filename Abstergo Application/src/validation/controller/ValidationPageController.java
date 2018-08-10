package validation.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXSpinner;

import bluetooth.LoginBluetoothModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import login.controller.PreLoginPageController;
import validation.ApplicationValidation;
import validation.ApplicationValidationDAO;

public class ValidationPageController {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private Label titleLbl;
	@FXML
	private Label additionalLbl;
	@FXML
	private JFXSpinner spinnerr;
	public static ApplicationValidation task;

	@FXML
	void initialize() {
		assert titleLbl != null : "fx:id=\"titleLbl\" was not injected: check your FXML file 'ValidationPage.fxml'.";
		assert additionalLbl != null : "fx:id=\"additionalLbl\" was not injected: check your FXML file 'ValidationPage.fxml'.";
		assert spinnerr != null : "fx:id=\"spinnerr\" was not injected: check your FXML file 'ValidationPage.fxml'.";

		try {
			task = new ApplicationValidation() {
				@Override
				protected Void call() {
					try {
						Thread.sleep(1000);
						ApplicationValidationDAO.clearFileMap();
	
						Platform.runLater(new Runnable() {
							public void run() {
								additionalLbl.setText("Retrieving file's hash from database.");
							}
						});
						new LoginBluetoothModel(); // Initializing Bluetooth to prevent slowing down login
						Map<String, String> fileHashMap = ApplicationValidationDAO.getFileHashMap();
	
						Platform.runLater(new Runnable() {
							public void run() {
								additionalLbl.setText("Calculating file hashes.");
							}
						});
						Thread.sleep(500);
						ArrayList<String> array = compareHashesPlus(fileHashMap);
	
						int fileSize = array.size();
						if (fileSize > 0) {
							Platform.runLater(new Runnable() {
								public void run() {
									additionalLbl.setText(array.size() + " files failed to be verified. Downloading files...");
								}
							});
							Thread.sleep(1000);
							for (String s : array) {
								// ApplicationValidationDAO.getFile(s); // Disabling it until presentation // TODO
								Platform.runLater(new Runnable() {
									public void run() {
										additionalLbl.setText(s + " downloaded.");
									}
								});
								Thread.sleep(500);
							}
							Platform.runLater(new Runnable() {
								public void run() {
									additionalLbl.setText("All files downloaded");
								}
							});
						} else {
							Platform.runLater(new Runnable() {
								public void run() {
									additionalLbl.setText("All files verified.");
								}
							});
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable() {
						public void run() {
							PreLoginPageController.playRest();
						}
					});
					
					return null;
				}
			};
			new Thread(task).start();
		} catch (Exception e) {
			additionalLbl.setText("Something went wrong. Please contact admin.");
		}
	}
}
