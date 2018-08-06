package email.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import email.controller.services.MessageRendererService;
import email.model.EmailMessageBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

public class EmailDetailsController extends AbstractController implements Initializable {
	
	public EmailDetailsController(ModelAccess modelAccess) {
		super(modelAccess);
	}

    @FXML
    private WebView webView;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label SenderLabel;
    
    private EmailMessageBean selectedMessage;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: implement delete function

		selectedMessage = getModelAccess().getSelectedMessage().copy();

		
		subjectLabel.setText("Subject: " + selectedMessage.getSubject());
		SenderLabel.setText("Sender: " + selectedMessage.getSender());
		
		String nameoffile = selectedMessage.getAttachmentsNames();
		System.out.println("hellokkkkkkkkkk");
		System.out.println(nameoffile);
		if(nameoffile.contains(".enc")) {
			File f = new File("src/util/default.html");
			webView.getEngine().load(f.toURI().toString());
			System.out.println("here?");
		}
		
		MessageRendererService messageRendererService = new MessageRendererService(webView.getEngine());
		messageRendererService.setMessageToRender(selectedMessage);
		messageRendererService.restart();

	}

}
