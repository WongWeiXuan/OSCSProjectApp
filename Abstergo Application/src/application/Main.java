package application;

import com.jfoenix.controls.JFXDecorator;

import bluetooth.BluetoothThreadModel;
import email.controller.services.CreateAndRegisterEmailAccountService;
import email.controller.services.FolderUpdaterService;
import email.view.ViewFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import log.LogModelThread;
import log.controller.LogNetworkPageController;
import log.controller.LogPageController;
import logExtra.BeepThread;
import validation.controller.ValidationPageController;

public class Main extends Application {
	final private String[] FONTARRAY = { "Regular", "Bold", "BoldItalic", "Italic" };
	private static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
//			Parent root = ViewFactory.defaultFactory.getMainScene();
			Parent root = FXMLLoader.load(getClass().getResource("/login/view/PreLoginPage.fxml"));
			JFXDecorator decorator = new JFXDecorator(stage, root);
			decorator.setCustomMaximize(true);
			Scene scene = new Scene(decorator);
			//testing
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("resource/pictures/abstergo-industries-logo-only.png"));
			primaryStage.setTitle("Abstergo Security Suite");
			primaryStage.setWidth(1280);
			primaryStage.setHeight(850);
			primaryStage.setMinWidth(1280);
			primaryStage.setMinHeight(850);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() { // Stop all the Threads when closing window (Sometimes it doesnt stops if "reconnected" device)
			    @SuppressWarnings("deprecation")
				@Override
			    public void handle(WindowEvent event) {
			    	if(BluetoothThreadModel.isRunning())
			    		BluetoothThreadModel.stopThread();
			    	if(LogModelThread.isRunning()) {
		    			LogModelThread.stop();
		    			LogPageController.thread.stop();
		    		}
			    	if(ValidationPageController.task != null && ValidationPageController.task.isRunning())
			    		ValidationPageController.task.cancel();
			    	if(BeepThread.isRunning())
			    		BeepThread.stop();
			    	if(LogNetworkPageController.t != null && LogNetworkPageController.t.isAlive())
			    		LogNetworkPageController.t.stop();
			    	if(LogNetworkPageController.beepThread != null && LogNetworkPageController.beepThread.isAlive())
			    		LogNetworkPageController.beepThread.stop();
			    	if(FolderUpdaterService.isitRunning()) {
			    		FolderUpdaterService.stop();
			    		System.out.println("successful stop thread");
			    	}
			    	Platform.exit();
			    }
			});
			
			for (String s : FONTARRAY) {
				Font font = Font.loadFont(getClass().getResourceAsStream("/resource/SourceSansPro-" + s + ".ttf"), 24);
				if (font == null) {
					System.out.println("NULL font family");
				}
			}
			
//			if(ViewFactory.defaultFactory.getPersistenceAcess().validPersistencefound()){
//				scene = ViewFactory.defaultFactory.getMainScene();
//			} 
//			//check the DB for existing account
//			else if(1 == 1) {
//				scene = ViewFactory.defaultFactory.getMainScene();
//			}
//			
//			else{
//				scene = ViewFactory.defaultFactory.getAddAccountScene();
//			}

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Stage getStage() {
		return stage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
