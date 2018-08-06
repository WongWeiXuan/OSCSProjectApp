package email.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import email.model.Password;
import email.model.EmailMessageBean;
import email.model.Encryption;

public class DecryptionController extends AbstractController implements Initializable{
	
	public DecryptionController(ModelAccess modelAccess) {
		super(modelAccess);
	}
	
	@FXML
	private Button decryptBTN;
	@FXML
	private TextField text;
	@FXML
	private Label label;
	@FXML
	private VBox decryption;

	/*
	Stage stage = (Stage) decryption.getScene().getWindow();
	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

        @Override
        public void handle(WindowEvent event) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    System.out.println("Page Closed by click to Close Button(X)");
                    try {
						deleteFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    stage.close();
                }
            });
        }
    });
    */
	
	public void decrypt(ActionEvent e) throws IOException {
		String filename = text.getText();
		filename = filename.replace(";","");
		if(filename.contains(".enc")) {
			new Password(filename, Encryption.DECRYPT_MODE);
		}

		
		//System.out.println("Decrypted");
	}
	
	public void deleteFile() throws IOException {
    	
    	
    		File filenamee = new File("src/util/Counting.txt");
    		FileWriter fw = new FileWriter(filenamee);
    	    PrintWriter p = new PrintWriter(fw);
    	    p.println("2");
    	    p.close();
    		System.out.println("delete this file");
    		
    		EmailMessageBean em = MainController.getMessage();
            ObservableList<EmailMessageBean> ol = MainController.getList();
            ModelAccess ac = new ModelAccess();
            new MainController(ac).deleteSelected(em, ol);
            
    	
    }
	
	public void initialize(URL location, ResourceBundle resources) {
		File filenamee = new File("src/util/Directory.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(filenamee))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				text.setText(sCurrentLine);
			}

		} catch (IOException ee) {
			ee.printStackTrace();
		}
		
	}
	
}
