package email.view;

import javax.naming.OperationNotSupportedException;

import email.controller.AbstractController;
import email.controller.AddAccountController;
import email.controller.ComposeEmailController;
import email.controller.DecryptionController;
import email.controller.EmailDetailsController;
import email.controller.EncryptionController;
import email.controller.MainController;
import email.controller.ModelAccess;
import email.controller.persistence.PersistenceAcess;
import email.model.EmailMessageBean;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ViewFactory {

	public static ViewFactory defaultFactory = new ViewFactory();
	
	public static boolean mainViewInitialized = false;

	private final String DEFAULT_CSS = "style.css";
	private final String EMAIL_DETAILS_FXML = "EmailDetailsLayout.fxml";
	private final String MAIN_SCREEN_FXML = "MainLayout.fxml";
	private final String COMPOSE_EMAIL_FXML = "ComposeEmailLayout.fxml";
	private final String ADD_ACCOUNT_EMAIL_FXML = "AddAccountLayout.fxml";
	private final String Encryption_FXML = "Encryption.fxml";
	private final String Decryption_FXML = "Decryption.fxml";

	private ModelAccess modelAccess = new ModelAccess();
	private PersistenceAcess persistenceAcess = new PersistenceAcess(modelAccess);

	public Parent getMainScene() throws OperationNotSupportedException {
		AbstractController mainController = new MainController(modelAccess);
		return initializeScene(MAIN_SCREEN_FXML, mainController);
	}

	public Parent getEmailDetailsScene() {
		AbstractController emailDetailsController = new EmailDetailsController(modelAccess);
		return initializeScene(EMAIL_DETAILS_FXML, emailDetailsController);
	}

	public Parent getComposeEmailScene() {
		AbstractController composeEmailController = new ComposeEmailController(modelAccess);
		return initializeScene(COMPOSE_EMAIL_FXML, composeEmailController);
	}
	
	public Parent getAddAccountScene(){
		AbstractController addAccountController = new AddAccountController(modelAccess);
		return initializeScene(ADD_ACCOUNT_EMAIL_FXML, addAccountController);
	}
	
	public Parent getEncryptionScene() {
		AbstractController encryptionController = new EncryptionController(modelAccess);
		return initializeScene(Encryption_FXML, encryptionController);
	}
	
	public Parent getDecryptionScene() {
		AbstractController decryptionController = new DecryptionController(modelAccess);
		return initializeScene(Decryption_FXML, decryptionController);
	}
	
	public Parent getComposeEmailScene(EmailMessageBean initialMessage, int type) {
		AbstractController composeEmailController;
		try {
			composeEmailController = new ComposeEmailController(modelAccess, initialMessage, type);
		} catch (Exception e) {
			composeEmailController = new ComposeEmailController(modelAccess);
			e.printStackTrace();
		}
		return initializeScene(COMPOSE_EMAIL_FXML, composeEmailController);
	}

	public Node resolveIcon(String treeItemValue) {
		String lowerCaseTreeItemValue = treeItemValue.toLowerCase();
		ImageView returnIcon;
		try {
			if (lowerCaseTreeItemValue.contains("inbox")) {
				returnIcon = new ImageView(new Image(getClass().getResourceAsStream("images/inbox.png")));
			} else if (lowerCaseTreeItemValue.contains("sent")) {
				returnIcon = new ImageView(new Image(getClass().getResourceAsStream("images/sent2.png")));
			} else if (lowerCaseTreeItemValue.contains("spam")) {
				returnIcon = new ImageView(new Image(getClass().getResourceAsStream("images/spam.png")));
			} else if (lowerCaseTreeItemValue.contains("@")) {
				returnIcon = new ImageView(new Image(getClass().getResourceAsStream("images/email.png")));
			} else if (lowerCaseTreeItemValue.isEmpty()) {
				return null;
			} else {
				returnIcon = new ImageView(new Image(getClass().getResourceAsStream("images/folder.png")));
			}
		} catch (NullPointerException e) {
			System.out.println("Invalid image location!!!");
			e.printStackTrace();
			returnIcon = new ImageView();
		}

		returnIcon.setFitHeight(16);
		returnIcon.setFitWidth(16);

		return returnIcon;
	}

	private Parent initializeScene(String fxmlPath, AbstractController controller) {
		FXMLLoader loader;
		Parent parent;
		try {
			loader = new FXMLLoader(getClass().getResource(fxmlPath));
			loader.setController(controller);
			parent = loader.load();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return parent;
	}

	public PersistenceAcess getPersistenceAcess() {
		return persistenceAcess;
	}

}
