package fileBackup;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import login.controller.PreLoginPageController;

public class FileBackupBeginController {
	@FXML
	private VBox recoverWrap;
	@FXML
	private VBox backupWrap;
	
	@FXML
    void changePage(MouseEvent event) throws IOException {
		AnchorPane toBeChanged = null;
		if (event.getSource().equals(recoverWrap)) {
			toBeChanged = FXMLLoader.load(getClass().getResource("/fileBackup/DisFileBackup.fxml"));
			PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
		}
		else if (event.getSource().equals(backupWrap)) {
			toBeChanged = FXMLLoader.load(getClass().getResource("/fileBackup/FileBackup.fxml"));
			PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
		}
    }
}
