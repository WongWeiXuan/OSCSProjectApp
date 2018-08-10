package email.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import email.model.Encryption;
import email.model.Password;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;


public class EncryptionController extends AbstractController implements Initializable{
	
	 private List<File> files = new ArrayList<File>();
	 
	 public EncryptionController(ModelAccess modelAccess) {
			super(modelAccess);
		}
	@FXML
	private Button selectBTN;
	@FXML
	private Button encryptBTN;
	@FXML
	private TextField text;
	@FXML
	private Label labell;
	
	 public void file(ActionEvent event) {
		FileChooser fc = new FileChooser();
		File selectedDirectory = fc.showOpenDialog(null);

		if(selectedDirectory == null){
		     //No Directory selected
		}else{
		     text.setText(selectedDirectory.getAbsolutePath());
		     files.add(selectedDirectory);
		     System.out.println(selectedDirectory);
		}
	}
	
	
	public void encryption(ActionEvent e) {
		long size = 0;
		for(int i=0; i<files.size();i++) {
			size += files.get(i).length();
		}
		if(size > 25000000) {
			labell.setText("File too big, please reduce size");
		}
		else if(text.getText().isEmpty()) {
			labell.setText("Please select a file to encrypt");
		}
		else {
			new Password(text.getText(), Encryption.ENCRYPT_MODE);
		}
	}
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
}