package login.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import login.LoginPageModel;

public class SignupPageController {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label loginText;
    @FXML
    private JFXTextField emailField;
    @FXML
    private TextFlow passwordCheckTextFlow;
    @FXML
    private Label passwordCheckTextLabel;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField passwordRetypeField;
    @FXML
    private JFXButton nextBtn;
    
    private void checkPassword() {
    	String password = passwordField.getText();
    	String retypedPassword = passwordRetypeField.getText();
    	
    	if(!password.isEmpty()) {
			if(password.equals(retypedPassword)) {
				nextBtn.setStyle("-fx-background-color: #2F5597");
			}else {
				nextBtn.setStyle("-fx-background-color: red");
			}
    	}else {
    		nextBtn.setStyle("-fx-background-color: #2F5597");
    	}
    }
    
    private void checkPasswordComplexity() {
    	String password = passwordField.getText();
    	
    	if(password.isEmpty()) {
    		passwordCheckTextFlow.setVisible(false);
    	}else {
        	String returnText = LoginPageModel.checkPasswordComplexity(password);
        	if(returnText.equals("Pass")) {
        		passwordCheckTextLabel.setStyle("-fx-text-fill: green");
        	}else {
        		passwordCheckTextLabel.setStyle("-fx-text-fill: red");
        	}
        	
        	passwordCheckTextLabel.setText(returnText);
        	passwordCheckTextFlow.setVisible(true);
    	}
    	checkPassword();
    }

    @FXML
    void nextPage(ActionEvent event) throws IOException {
    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage2.fxml"));
		stage.setScene(new Scene(root));
    }
    
    @FXML
    void initialize() {
        assert emailField != null : "fx:id=\"emailField\" was not injected: check your FXML file 'SignupPage.fxml'.";
        assert passwordCheckTextFlow != null : "fx:id=\"passwordCheckTextFlow\" was not injected: check your FXML file 'SignupPage.fxml'.";
        assert passwordCheckTextLabel != null : "fx:id=\"passwordCheckTextLabel\" was not injected: check your FXML file 'SignupPage.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'SignupPage.fxml'.";
        assert passwordRetypeField != null : "fx:id=\"passwordRetypeField\" was not injected: check your FXML file 'SignupPage.fxml'.";
        assert nextBtn != null : "fx:id=\"nextBtn\" was not injected: check your FXML file 'SignupPage.fxml'.";

        ImageView imageView = new ImageView(new Image("pictures/back-arrow.png"));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        loginText.setGraphic(imageView);
        loginText.setGraphicTextGap(20);
        loginText.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				try {
					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/LoginPage.fxml"));
					stage.setScene(new Scene(root));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        });
        
        passwordField.textProperty().addListener(new ChangeListener<Object>() {

			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				checkPasswordComplexity();
			}
    	});
        
        passwordRetypeField.textProperty().addListener(new ChangeListener<Object>() {

			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				checkPassword();
			}
    	});
    }
}
