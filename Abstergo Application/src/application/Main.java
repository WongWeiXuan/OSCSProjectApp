package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		String [] fontArray = {"Regular", "Bold", "BoldItalic", "Italic"};
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			// primaryStage.getIcons().add(new Image(""));
			primaryStage.setTitle("Abstergo Security Suite");
			// primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setWidth(1280);
			primaryStage.setHeight(850);
			
			for(String s: fontArray) {
				Font font = Font.loadFont(getClass().getResourceAsStream("/resource/SourceSansPro-" + s + ".ttf"), 24);
				if(font == null) {
					System.out.println("NULL font family");
				}
			}
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
