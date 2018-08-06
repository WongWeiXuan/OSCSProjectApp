package email.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javax.mail.Flags;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXDecorator;

import email.controller.services.FolderUpdaterService;
import email.controller.services.MessageRendererService;
import email.controller.services.SaveAttachmentsService;
import email.model.EmailConstants;
import email.model.EmailMessageBean;
import email.model.folder.EmailFolderBean;
import email.model.table.BoldableRowFactory;
import email.model.table.FormatableInteger;
import email.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//import DONOTCOMMIT.DONOTCOMMIT;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import login.controller.LoginPageController;

public class MainController extends AbstractController implements Initializable{
	
    public MainController(ModelAccess modelAccess) {
		super(modelAccess);
	}

	@FXML
    private TreeView<String> emailFoldersTreeView;
    private MenuItem showDetails = new MenuItem("show details");
    private MenuItem markUnread = new MenuItem("mark as unread");
    private MenuItem deleteMessage = new MenuItem("delete message");
    private MenuItem reply = new MenuItem("reply ");

    @FXML
    private Label downAttachLabel;
    @FXML
    private ProgressIndicator attachProgress;
    private SaveAttachmentsService saveAttachmentsService;
	
    @FXML
    private Label attachementsLabel;
    @FXML
    private TableView<EmailMessageBean> emailTableView;	
    @FXML
    private TableColumn<EmailMessageBean, String> subjectCol;
    @FXML
    private TableColumn<EmailMessageBean, String> senderCol;
    @FXML
    private TableColumn<EmailMessageBean, String> recipientCol;
    @FXML
    private TableColumn<EmailMessageBean, FormatableInteger> sizeCol;    
    @FXML
    private TableColumn<EmailMessageBean, Date> dateCol;	
    @FXML
    private WebView messageRenderer;	
    @FXML
    private Button downloadAttachBtn;
    @FXML
    private Button btnED;
    @FXML
    protected TextField pathTextField;
    
    protected Stage primaryStage;

    
    Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
	String username = userCache.get("User");
	
    
    
    
    public void openED(ActionEvent event) throws IOException {
    	Parent parent = ViewFactory.defaultFactory.getEncryptionScene();
    	Stage stage = new Stage();
		JFXDecorator decorator = new JFXDecorator(stage, parent);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator);
    	stage.setScene(scene);
    	stage.show();
    }
    protected void addExtensionFilters(ObservableList<FileChooser.ExtensionFilter> extensionFilters) {
    }
    
    @FXML
    void composeBtnAction() {
    	Parent parent = ViewFactory.defaultFactory.getComposeEmailScene();
    	Stage stage = new Stage();
		JFXDecorator decorator = new JFXDecorator(stage, parent);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator);
    	stage.setScene(scene);
    	stage.show();

    }
    
    static EmailMessageBean emb = new EmailMessageBean(true);
    static ObservableList<EmailMessageBean> list = FXCollections.observableArrayList();
    
    public void deleteSelected(EmailMessageBean message, ObservableList<EmailMessageBean> list1) {
    	//EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
		try {
			message.getMessageRefference().setFlag(Flags.Flag.DELETED, true);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		list1.remove(message);
		
    }
    
    public static EmailMessageBean getMessage() {
    	return emb;
    }
    public static ObservableList<EmailMessageBean> getList(){
    	return list;
    }
    
    @FXML
    void downloadAttachBtnAction() throws IOException {
    	saveAttachmentsService.setEmailMessage(getModelAccess().getSelectedMessage());
    	if(getModelAccess().getSelectedMessage().getListOfAttachments().size() > 0 ){
    		saveAttachmentsService.restart();
    		if(attachementsLabel.getText().contains(".enc")) {
    			String ll = System.getProperty("user.home") + "\\Downloads\\";
    			String nameoffile = attachementsLabel.getText();
    			String fullpath = ll + nameoffile;
    			//write file into text file
    			File filenamee = new File("src/util/Directory.txt");
    			FileWriter fw = new FileWriter(filenamee);
    		    PrintWriter p = new PrintWriter(fw);
    		    p.println(fullpath);
    		    p.close();
    		    
    		    
    		    
    			
    		    Parent parent = ViewFactory.defaultFactory.getDecryptionScene();
    		    Stage stage = new Stage();
    			JFXDecorator decorator = new JFXDecorator(stage, parent);
    			decorator.setCustomMaximize(true);
    			Scene scene = new Scene(decorator);
    	    	stage.setScene(scene);
    	    	stage.show();
        	}
    	}    	
    	
    }
    
    @FXML
    void addAccountBtnAction(){
    	Parent parent = ViewFactory.defaultFactory.getAddAccountScene();
    	Stage stage = new Stage();
		JFXDecorator decorator = new JFXDecorator(stage, parent);
		decorator.setCustomMaximize(true);
		Scene scene = new Scene(decorator);
    	stage.setScene(scene);
    	stage.show();
    }
    private MessageRendererService messageRendererService;
    private FolderUpdaterService folderUpdaterService;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println(username);
		downloadAttachBtn.setDisable(true);
		attachementsLabel.setText("");
		saveAttachmentsService = new SaveAttachmentsService(attachProgress, downAttachLabel);
		attachProgress.progressProperty().bind(saveAttachmentsService.progressProperty());
		folderUpdaterService = new FolderUpdaterService(getModelAccess());
		folderUpdaterService.start();
		messageRendererService = new MessageRendererService(messageRenderer.getEngine());
		
		emailTableView.setRowFactory(e-> new BoldableRowFactory<>());
		subjectCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, String>("subject"));
		senderCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, String>("sender"));
		recipientCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, String>("recipient"));
		sizeCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, FormatableInteger>("size"));	
		dateCol.setCellValueFactory(new PropertyValueFactory<EmailMessageBean, Date>("date"));
		
		//BUG: sizeCol doesn't get it's default comparator overridden, have to do this manually!!!
		sizeCol.setComparator(new FormatableInteger(0));	
		
		
		emailFoldersTreeView.setRoot(getModelAccess().getRoot());		
		emailFoldersTreeView.setShowRoot(false);
		
//		CreateAndRegisterEmailAccountService createAndRegisterEmailAccountService1 = 
//				new CreateAndRegisterEmailAccountService(DONOTCOMMIT.address1, 
//				DONOTCOMMIT.password1,
//				getModelAccess());
//		createAndRegisterEmailAccountService1.restart();
//
//		CreateAndRegisterEmailAccountService createAndRegisterEmailAccountService2 = 
//				new CreateAndRegisterEmailAccountService(DONOTCOMMIT.address2, 
//				DONOTCOMMIT.password2,
//				getModelAccess());
//		createAndRegisterEmailAccountService2.restart();
//		
//		CreateAndRegisterEmailAccountService createAndRegisterEmailAccountService3 = 
//				new CreateAndRegisterEmailAccountService(DONOTCOMMIT.address3, 
//				DONOTCOMMIT.password3,
//				getModelAccess());
//		createAndRegisterEmailAccountService3.restart();
		
		emailTableView.setContextMenu(new ContextMenu(showDetails, markUnread, deleteMessage, reply));
		
		emailFoldersTreeView.setOnMouseClicked(e ->{
			EmailFolderBean<String> item = (EmailFolderBean<String>)emailFoldersTreeView.getSelectionModel().getSelectedItem();
			if(item != null && !item.isTopElement()){
				emailTableView.setItems(item.getData());
				getModelAccess().setSelectedFolder(item);
				//clear selected message:
				getModelAccess().setSelectedMessage(null);
			}
		});
		emailTableView.setOnMouseClicked(e->{
			EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
			if(message != null){
				if(!message.isRead()){
					message.setRead(true);
					try {
						message.getMessageRefference().setFlag(Flags.Flag.SEEN, true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					getModelAccess().getSelectedFolder().decrementUreadMessagesCount();
				}
			if(message.hasAttachments()){
				downloadAttachBtn.setDisable(false);
				attachementsLabel.setText(message.getAttachmentsNames());
				if(message.getAttachmentsNames().contains(".enc")) {
					list = getModelAccess().getSelectedFolder().getData();
					emb = message;
					emailTableView.setStyle("-fx-background-color: red ;");
				}
			}else{
				downloadAttachBtn.setDisable(true);
				attachementsLabel.setText("");
				emailTableView.setStyle("-fx-background-color: white ;");
			}	
				getModelAccess().setSelectedMessage(message);
				messageRendererService.setMessageToRender(message);
				messageRendererService.restart();
			}
		});
		showDetails.setOnAction(e->{			
			Parent parent = ViewFactory.defaultFactory.getEmailDetailsScene();
			Stage stage = new Stage();
			JFXDecorator decorator = new JFXDecorator(stage, parent);
			decorator.setCustomMaximize(true);
			Scene scene = new Scene(decorator);
			stage.setScene(scene);
			stage.show();
		});
		markUnread.setOnAction(e->{
			EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
			getModelAccess().getSelectedFolder().incrementUnreadMessageCount(1);
			message.setRead(false);
			try {
				message.getMessageRefference().setFlag(Flags.Flag.SEEN, false);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		deleteMessage.setOnAction(e->{
			EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
			try {
				message.getMessageRefference().setFlag(Flags.Flag.DELETED, true);
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			getModelAccess().getSelectedFolder().getData().remove(message);
		});
		reply.setOnAction(e->{
			if (messageRendererService.getState() != State.RUNNING) {
				EmailMessageBean message = emailTableView.getSelectionModel().getSelectedItem();
				message.setContentForForvarding(messageRendererService.getContent());
				Parent parent = ViewFactory.defaultFactory.getComposeEmailScene(message, EmailConstants.REPLY_MESSAGE);
				Stage stage = new Stage();
				JFXDecorator decorator = new JFXDecorator(stage, parent);
				decorator.setCustomMaximize(true);
				Scene scene = new Scene(decorator);
				stage.setScene(scene);
				stage.show();
			}
		});
		
		
		
	}
	

}
