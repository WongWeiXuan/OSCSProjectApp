package fileStorage;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FileStorageConfirmController {
	@FXML
	private Label confirmMsg;
	@FXML
	private HBox confirmWrap;
	
	@FXML
	public void initialize() {
		confirmMsg.setText(FileStorageController.confirmMsg);
	}
	
	@FXML
	public void closePopup(MouseEvent event) {
		Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage.close();
	}

}
