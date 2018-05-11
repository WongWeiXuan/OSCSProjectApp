package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage){
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/login/LoginPage.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			//primaryStage.getIcons().add(new Image(""));
			primaryStage.setTitle("Abstergo Security Suite");
			//primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
