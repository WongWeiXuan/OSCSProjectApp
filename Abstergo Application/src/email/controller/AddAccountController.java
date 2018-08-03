package email.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javax.naming.OperationNotSupportedException;

import email.controller.services.CreateAndRegisterEmailAccountService;
import email.model.EmailConstants;
import email.view.ViewFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddAccountController extends AbstractController implements Initializable{
	

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField addressField;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    void loginBtnAction() {

    	//TODO add validation
    	//TODO add props handling
    	statusLabel.setText("");
    	//enter into database
    	CreateAndRegisterEmailAccountService createAndRegisterEmailAccountService = 
    			new CreateAndRegisterEmailAccountService(
    					addressField.getText(),
    					passwordField.getText(), 
    					getModelAccess());
    	createAndRegisterEmailAccountService.start();
    	statusLabel.setText("logging in....");
    	createAndRegisterEmailAccountService.setOnSucceeded(e->{
    		if(createAndRegisterEmailAccountService.getValue() != EmailConstants.LOGIN_STATE_SUCCEDED){
    			statusLabel.setText("Either the email or password is incorrect");
    		}else{
    			Stage stage = (Stage)addressField.getScene().getWindow();//just getting a reference to the stage

    			if(ViewFactory.mainViewInitialized){
        			//close the window
        			stage.close();
    			} else{
    				try {
						stage.setScene(new Scene(ViewFactory.defaultFactory.getMainScene()));
					} catch (OperationNotSupportedException e1) {
						e1.printStackTrace();
					}
    			}
    		}
    	});
    }
    

	public AddAccountController(ModelAccess modelAccess) {
		super(modelAccess);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
