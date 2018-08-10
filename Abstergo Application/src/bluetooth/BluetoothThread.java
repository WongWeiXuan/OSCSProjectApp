package bluetooth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.bluetooth.BluetoothStateException;

import org.ehcache.Cache;
import org.ehcache.spi.loaderwriter.CacheLoadingException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;
import setting.Setting;

public class BluetoothThread implements Runnable {
	private volatile boolean running;
	private BluetoothDevice device;

	public BluetoothThread(BluetoothDevice device) {
		running = true;
		this.device = device;
	}

	@Override
	public void run() {
		try {
			ArrayList<BluetoothDevice> array = new ArrayList<BluetoothDevice>();
			array.add(device);
			if (!LoginBluetoothModel.isInitialized()) {
				LoginBluetoothModel.initialiseBluetooth();
			}
			LoginBluetoothModel.setPairedArray(array);

			while (running) {
				try {
					System.out.println("Running");
					boolean exist = LoginBluetoothModel.scanForPairedBluetoothDevice();
					if (!exist) {
						System.out.println("Device not found! - First pass");
						Thread.sleep(3000);
						exist = LoginBluetoothModel.scanForPairedBluetoothDevice();
						if(!exist) {
//							BluetoothThreadModel.logoutNotFound(); // Disabling it until presentation // TODO
							System.out.println("Device not found! - Final pass");

							Setting setting = new Setting(); // Getting time set
							String howToDC = setting.getPreference().getOnDisconnection();
							if("Logout".equals(howToDC)) {
								Timer timer = new Timer();
								if(setting.getPreference().getTimeout() > 0) {
									timer.schedule(new TimerTask() {
										@Override
										public void run() {
											BluetoothThreadModel.stopThread();
										}
			
									}, setting.getPreference().getTimeout()); // 15 mins btw (Default) // Now settings work so... no longer default
								}
								long polling = 1000;
								while (running) {
									boolean exist2 = LoginBluetoothModel.scanForPairedBluetoothDevice();
									System.out.println("WHILE: " + exist2);
									if (exist2) {
										timer.cancel();
										logBackIn();
										break;
									}
									Thread.sleep(polling);
									if (polling < 15000)
										polling *= 1.5;
								}
							} else if("Signout".equals(howToDC)) {
								Runtime.getRuntime().exec("shutdown -l -f");
								System.exit(0);
							} else if("Sleep".equals(howToDC)) {
								Runtime.getRuntime().exec("psshutdown -d -p");
								System.exit(0);
							} else if("Shutdown".equals(howToDC)) {
								Runtime.getRuntime().exec("shutdown -s -p");
								System.exit(0);
							}
						}
					}
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (BluetoothStateException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	// If within timeframe the device is detected again
	private void logBackIn() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
					AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource(userCache.get("Last"))); // Change scene
					PreLoginPageController.stackPaneClone.getChildren().remove(0);
					PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
					PreLoginPageController.navBarClone.setVisible(true);
				} catch (CacheLoadingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Method to stop the thread
	public synchronized void stop() {
		running = false;
	}
}
