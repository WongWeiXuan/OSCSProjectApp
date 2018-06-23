package bluetooth;

import java.io.IOException;

import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import login.controller.LoginPageController;

public class BluetoothThreadModel {
	private static BluetoothThread bluetoothThread;
	
	public static void startThread(BluetoothDevice device) {
		bluetoothThread = new BluetoothThread(device);
		Thread t = new Thread(bluetoothThread, "Thread");
		t.start();
	}

	public static void stopThread() {
		bluetoothThread.stop();
	}
	
	public static void logoutNotFound() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					LoginPageController.setDeviceNotFound(true);
					Scene scene = Main.getStage().getScene();
					scene.setRoot(FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml")));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
