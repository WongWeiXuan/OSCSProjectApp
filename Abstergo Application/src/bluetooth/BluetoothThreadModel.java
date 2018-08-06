package bluetooth;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;

public class BluetoothThreadModel {
	private static boolean running;
	private static BluetoothThread bluetoothThread;
	private static Thread t;

	public static void startThread(BluetoothDevice device) {
		bluetoothThread = new BluetoothThread(device);
		t = new Thread(bluetoothThread, "Thread");
		t.start();
		running = true;
	}

	@SuppressWarnings("deprecation")
	public static void stopThread() {
		bluetoothThread.stop();
		t.stop();
		running = false;
	}
	
	public static boolean isRunning() {
		return running;
	}

	public static void logoutNotFound() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					LoginPageController.setDeviceNotFound(true);
					AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml")); // Change scene
					toBeChanged.setOpacity(1);
					PreLoginPageController.stackPaneClone.getChildren().add(0, toBeChanged);
					PreLoginPageController.anchorPaneClone.getChildren().clear();
					PreLoginPageController.navBarClone.setVisible(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
