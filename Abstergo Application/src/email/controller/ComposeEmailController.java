package email.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import email.controller.services.EmailSenderService;
import email.model.EmailConstants;
import email.model.EmailMessageBean;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ComposeEmailController extends AbstractController implements Initializable {

	private int type = EmailConstants.STANDALONE_MESSAGE;
	private EmailMessageBean initialMessage;
    private List<File> attachments = new ArrayList<File>();

	public ComposeEmailController(ModelAccess modelAccess) {
		super(modelAccess);
	}

	/**
	 * Used for replying or forwarding messages
	 * 
	 * @param modelAccess
	 * @param initialMessage
	 * @param type
	 * @throws Exception 
	 */
	public ComposeEmailController(ModelAccess modelAccess, EmailMessageBean initialMessage, int type) throws Exception {
		super(modelAccess);
		if(initialMessage.getContentForForvarding() == null){
			throw new Exception("Content was not found in the forwarded message!!!");
		}
		this.type = type;
		this.initialMessage = initialMessage;

	}

	@FXML
	private TextField RecipientField;

	@FXML
	private TextField SubjectField;

	@FXML
	private HTMLEditor ComposeArea;

	@FXML
	private TextField CcField;

	@FXML
	private ChoiceBox<String> SenderChoice;

	@FXML
	private Label errorLabel;

	@FXML
	private Label attachNamesLabel;
	
	@FXML
	private Button btncancel;
	
	@FXML
	private Button btnsend;
	
	public void cancelpage(ActionEvent event) {
		Stage stage = (Stage) btncancel.getScene().getWindow();
	    stage.close();
	}

	@FXML
	void AttachBtnAction() {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.getExtensionFilters().addAll(
    			//for images
    			new FileChooser.ExtensionFilter("PNG", "*.png"),
    			new FileChooser.ExtensionFilter("JPG", "*.jpg"),
    			//for MS software
    			new FileChooser.ExtensionFilter("WORD", "*.docx"),
    			new FileChooser.ExtensionFilter("PPT", "*.pptx"),
    			new FileChooser.ExtensionFilter("TEXT", "*.txt"),
    			new FileChooser.ExtensionFilter("VIDEO", "*.mp4"),
    			new FileChooser.ExtensionFilter("Encrypted", "*.enc")
    			);
    	File selectedFile = fileChooser.showOpenDialog(null);
    	if(selectedFile != null){
    		attachments.add(selectedFile);
    			attachNamesLabel.setText(attachNamesLabel.getText() + "   "  +  selectedFile.getName()+ "; ");    		
    	}
    	else {
    		Stage stage = (Stage) btnsend.getScene().getWindow();
    	    stage.close();
    	}

	}

	@FXML
	void SendBtnAction(ActionEvent ee) {
		long size = 0;
		for(int i=0; i<attachments.size();i++) {
			size += attachments.get(i).length();
		}
		if(size > 25000000) {
			errorLabel.setText("File too big, please reduce size");
			errorLabel.setStyle("-fx-font-color: red;");
		}
		else {
		errorLabel.setText("Processing.....");
		EmailSenderService emailSenderService = 
				new EmailSenderService(getModelAccess().getEmailAccountByName(SenderChoice.getValue()),
						SubjectField.getText(),
						RecipientField.getText(),
						CcField.getText(), 
						ComposeArea.getHtmlText(),
						attachments);
		emailSenderService.restart();
		emailSenderService.setOnSucceeded(e->{
			if(emailSenderService.getValue() != EmailConstants.MESSAGE_SENT_OK){
				errorLabel.setText("Error while sending message");
			}
			else {
				Stage stage = (Stage) btnsend.getScene().getWindow();
			    stage.close();
			}
			
		});
		
		}//end of else statement
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fillInitialDetails();
		SenderChoice.setItems(getModelAccess().getEmailAccountNames());
		SenderChoice.setValue(getModelAccess().getEmailAccountNames().get(0));

	}

	private void fillInitialDetails() {
		if (type != EmailConstants.STANDALONE_MESSAGE) {
			switch (type) {
			case EmailConstants.FORWARD_MESSAGE:
				SubjectField.setText("FW: " + initialMessage.getSubject());
				break;
			case EmailConstants.REPLY_MESSAGE:
				SubjectField.setText("RE: " + initialMessage.getSubject());
				RecipientField.setText(initialMessage.getSender());
				break;
			default:
				break;
			}
			ComposeArea.setHtmlText("<br><br><br><br><br>From " + initialMessage.getSender() 
					+ "<br>Sent: " + initialMessage.getDate()
					+ "<br>To: " + initialMessage.getRecipient() 
					+ "<br>Subject: " + initialMessage.getSubject()
					+ "<br><br>" + initialMessage.getContentForForvarding());
		}

	}
	
//	private String validateEmailAddress(String address){
//		
//	}
	
	

}
