package login.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.RemoteDevice;

import org.ehcache.Cache;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import javafx.animation.TranslateTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import login.BluetoothDevice;
import login.LoginBluetoothModel;

public class SignupPage2Controller {
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private HBox instructionHBox;
	@FXML
	private GridPane animationGridPane;
	@FXML
	private Circle circle1;
	@FXML
	private Circle circle2;
	@FXML
	private Circle circle3;
	@FXML
	private VBox confirmationVBox;
	@FXML
	private Text cachePairedText;
	@FXML
	private JFXButton changeBtn;
	@FXML
	private JFXButton confirmBtn;
	@FXML
	private JFXListView<Label> listView;
	@FXML
	private HBox popupHBox;
	@FXML
	private JFXButton closeBtn;
	private Service<Map<RemoteDevice, String>> backgroundService;
	private Service<Boolean> backgroundService2;

	final private String PPATH = "pictures/PhoneIcon.png";
	final private String WPATH = "pictures/WatchIcon.png";
	final private String UPATH = "pictures/UnidentifiedIcon.png";

	private void changeScene() throws IOException {
		Cache<String, String> cache = SignupPageController.cacheManager.getCache("deviceRegistration", String.class, String.class);
		cachePairedText.setText(cache.get("DeviceName") + " is paired. Confirm?");
		instructionHBox.setVisible(false);
		confirmationVBox.setVisible(true);
	}

	@FXML
	void continueWithPairing(ActionEvent event) {
		confirmationVBox.setVisible(false);
		animationGridPane.setVisible(true);
		closeBtn.fire();
	}

	@FXML
	void goToPage3(ActionEvent event) throws IOException {
		Parent root = (Parent) FXMLLoader.load(getClass().getResource("../view/SignupPage3.fxml")); // Change scene
		circle1.getScene().setRoot(root);
	}

	void printDeviceList(Map<RemoteDevice, String> listOfDevice) throws IOException {
		for (Entry<RemoteDevice, String> entry : listOfDevice.entrySet()) {
			String path = "";
			if ("512".equals(entry.getValue())) {
				path = PPATH;
			} else if ("1792".equals(entry.getValue())) {
				path = WPATH;
			} else if ("7936".equals(entry.getValue())) {
				path = UPATH;
			}

			Image image = new Image(path);
			ImageView imageView = new ImageView(image);
			imageView.setPreserveRatio(true);
			imageView.setFitWidth(0);
			imageView.setFitHeight(150);

			Label nameLbl = new Label(entry.getKey().getFriendlyName(false));
			nameLbl.setGraphic(imageView);
			nameLbl.setGraphicTextGap(50);
			nameLbl.setStyle("-fx-font-family: 'Source Sans Pro';-fx-font-size: 26px;");

			listView.getItems().add(nameLbl);

		}
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			int tries = 0;

			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					backgroundService2 = new Service<Boolean>() {

						@Override
						protected Task<Boolean> createTask() {
							return new Task<Boolean>() {

								@Override
								protected Boolean call() throws Exception {
									System.out.println("Initializing Pairing..."); // Debug purpose
									instructionHBox.setVisible(true);
									listView.setVisible(false);
									int selectedIndex = listView.getSelectionModel().getSelectedIndex();
									boolean result = LoginBluetoothModel.pairBluetoothDevice(selectedIndex);

									// Storing in cache
									if (result) {
										ArrayList<BluetoothDevice> pairedArray = LoginBluetoothModel.getPairedArray();
										BluetoothDevice device = pairedArray.get(selectedIndex);
										Cache<String, String> deviceRegistration = SignupPageController.cacheManager
												.createCache("deviceRegistration",
														CacheConfigurationBuilder.newCacheConfigurationBuilder(
																String.class, String.class,
																ResourcePoolsBuilder.heap(3)));
										deviceRegistration.put("BluetoothAddress", device.getBluetoothAddress());
										deviceRegistration.put("DeviceName", device.getFriendlyName());
										deviceRegistration.put("MajorClass", String.valueOf(device.getMajorClass()));
									}
									return result;
								}
							};
						}
					};

					backgroundService2.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

						public void handle(WorkerStateEvent event) {
							if (backgroundService2.getValue()) {
								System.out.println("Paired"); // Debug purpose
								try {
									changeScene();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								if (tries < 2) {
									System.out.println("Failed to pair"); // Debug purpose
									backgroundService2.restart();
									tries++;
								} else {
									instructionHBox.setVisible(false);
									listView.setVisible(true);
								}
							}
						}
					});

					backgroundService2.start();
					tries++;
				}
			}
		});
	}

	@FXML
	void closePopup(ActionEvent event) throws InterruptedException, IOException {
		popupHBox.setVisible(false);

		backgroundService = new Service<Map<RemoteDevice, String>>() {

			@Override
			protected Task<Map<RemoteDevice, String>> createTask() {
				return new Task<Map<RemoteDevice, String>>() {

					@Override
					protected Map<RemoteDevice, String> call() throws Exception {
						System.out.println("Starting bluetooth scanning..."); // Debug purpose
						if(!LoginBluetoothModel.isInitialized()) {
							LoginBluetoothModel.initialiseBluetooth();
						}
						Thread.sleep(3000); // It returns too fast so making it slower to look nicer?
						Map<RemoteDevice, String> scanning = LoginBluetoothModel.scanBluetoothDevice();
						return scanning;
					}
				};
			}
		};

		backgroundService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			public void handle(WorkerStateEvent event) {
				try {
					printDeviceList(backgroundService.getValue());
					animationGridPane.setVisible(false);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		backgroundService.start();
	}

	@FXML
	void initialize() {
		assert instructionHBox != null : "fx:id=\"instructionHBox\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert animationGridPane != null : "fx:id=\"animationGridPane\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert circle1 != null : "fx:id=\"circle1\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert circle2 != null : "fx:id=\"circle2\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert circle3 != null : "fx:id=\"circle3\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert cachePairedText != null : "fx:id=\"cachePairedText\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert changeBtn != null : "fx:id=\"changeBtn\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert confirmBtn != null : "fx:id=\"confirmBtn\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert popupHBox != null : "fx:id=\"popupHBox\" was not injected: check your FXML file 'SignupPage2.fxml'.";
		assert closeBtn != null : "fx:id=\"closeBtn\" was not injected: check your FXML file 'SignupPage2.fxml'.";

		Cache<String, String> cache = SignupPageController.cacheManager.getCache("deviceRegistration", String.class,
				String.class);
		if (cache != null) {
			popupHBox.setVisible(false);
			cachePairedText.setText(cache.get("DeviceName") + " is paired. Confirm?");
			confirmationVBox.setVisible(true);
		}

		final TranslateTransition transition1 = new TranslateTransition();
		transition1.setDuration(Duration.millis(500));
		transition1.setAutoReverse(true);
		transition1.setCycleCount(2);
		transition1.setToY(-30);
		transition1.setNode(circle1);
		transition1.play();

		final TranslateTransition transition2 = new TranslateTransition();
		transition2.setDuration(Duration.millis(500));
		transition2.setAutoReverse(true);
		transition2.setCycleCount(2);
		transition2.setToY(-30);
		transition2.setNode(circle2);
		transition2.setDelay(Duration.millis(500));
		transition2.play();

		final TranslateTransition transition3 = new TranslateTransition();
		transition3.setDuration(Duration.millis(500));
		transition3.setAutoReverse(true);
		transition3.setCycleCount(2);
		transition3.setToY(-30);
		transition3.setNode(circle3);
		transition3.setDelay(Duration.millis(1000));
		transition3.play();

		transition3.setOnFinished(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					transition1.playFromStart();
					transition2.playFromStart();
					transition3.playFromStart();
				}
			}
		});
	}
}
