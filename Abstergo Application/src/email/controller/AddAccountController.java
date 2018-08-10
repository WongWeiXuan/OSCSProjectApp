package email.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.naming.OperationNotSupportedException;

import org.ehcache.Cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import email.controller.services.CreateAndRegisterEmailAccountService;
import email.model.Email;
import email.model.EmailConstants;
import email.model.customEmail;
import email.view.ViewFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;

public class AddAccountController extends AbstractController implements Initializable{
	

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField addressField;
    
    @FXML
    private Label statusLabel;
    
    Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
   	String username = userCache.get("User");
    
   	public boolean isDatabaseEmpty() {
   		System.out.println(username);
		JsonArray json = EmailDAO.getdetails(username);
			if(json.isJsonNull() || json.size() == 0) {
				return true;
			}
			return false;
   	}
   	
    @FXML
    void loginBtnAction() {
    	boolean checks = isDatabaseEmpty();
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
    			//adding into database
    			Email e2 = new Email(username, addressField.getText(), passwordField.getText());
    			EmailDAO.createEmail(e2);
    			Stage stage = (Stage)addressField.getScene().getWindow();//just getting a reference to the stage

    			if(!checks){
        			//close the window
        			stage.close();
    			} else{
    				
    					stage.close();
    					try {
							new PreLoginPageController().nextPage();
						} catch (OperationNotSupportedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//stage.setScene(new Scene(ViewFactory.defaultFactory.getMainScene()));
					
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
