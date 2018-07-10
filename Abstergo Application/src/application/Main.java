package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	final private String[] FONTARRAY = { "Regular", "Bold", "BoldItalic", "Italic" };
	private static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			stage = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("resource/pictures/abstergo-industries-logo-only.png"));
			primaryStage.setTitle("Abstergo Security Suite");
			primaryStage.setWidth(1280);
			primaryStage.setHeight(850);
			primaryStage.setMinWidth(1280);
			primaryStage.setMinHeight(850);

			for (String s : FONTARRAY) {
				Font font = Font.loadFont(getClass().getResourceAsStream("/resource/SourceSansPro-" + s + ".ttf"), 24);
				if (font == null) {
					System.out.println("NULL font family");
				}
			}
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
