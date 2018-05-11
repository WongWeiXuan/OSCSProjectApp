package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class LoginPageController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private StackPane loginStackpane;

    @FXML
    private JFXTextField usernameField;

    @FXML
    private JFXPasswordField passwordField;

    @FXML
    private JFXButton loginBtn;

    @FXML
    private JFXDialog loginDialog;

    @FXML
    private JFXDialogLayout loginDialogLayout;

    @FXML
    private JFXButton dialogCloseBtn;

    @FXML
    void closeDialog(ActionEvent event) {
    	loginDialog.close();
    }

    @FXML
    void validateLogin(ActionEvent event) {
    	String username = usernameField.getText();
    	String password = passwordField.getText();
    	if(!username.isEmpty() && !password.isEmpty()) {
			loginDialog.show();
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
