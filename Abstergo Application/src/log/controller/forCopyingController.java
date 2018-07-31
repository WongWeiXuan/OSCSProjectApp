package log.controller;

import javafx.fxml.FXML;

import javafx.scene.input.MouseEvent;

public class forCopyingController {

	// Event Listener on ImageView.onMouseClicked
	@FXML
	public void showRightClick(MouseEvent event) {
		LogNetworkPageController.showRightClick(event);
	}
}
