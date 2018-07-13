package setting.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleNode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import login.CacheMan;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;
import setting.Setting;
import setting.SettingModel;

public class SettingPageController {
	@FXML
    private AnchorPane root;
	@FXML
	private ToggleGroup timeout;
	@FXML
	private JFXToggleNode timeout1;
	@FXML
	private JFXToggleNode timeout2;
	@FXML
	private JFXToggleNode timeout3;
	@FXML
	private JFXToggleNode timeout4;
	@FXML
	private JFXToggleNode timeout5;
	@FXML
	private JFXToggleNode timeout6;
	@FXML
	private JFXCheckBox incomingCheckbox;
	@FXML
	private Rectangle greenRec;
	@FXML
	private Rectangle yellowRec;
	@FXML
	private Rectangle redRec;
	@FXML
	private JFXSlider incomingSlider;
	@FXML
	private Spinner<Integer> incomingSliderSpinner;
	@FXML
	private JFXComboBox<Label> onDisconnectCombo;
	@FXML
	private JFXButton applyBtn;
	@FXML
	private JFXButton restoreBtn;
	private Setting setting;

	@FXML
	void setSlider(KeyEvent event) {
		incomingSlider.setValue(incomingSliderSpinner.getValue());
	}

	@FXML
	void setSpinner(DragEvent event) {
		System.out.println("Slider: " + incomingSlider.getValue());
		incomingSliderSpinner.getValueFactory().setValue((int) incomingSlider.getValue());
	}
	
	@FXML
    void applySettings(ActionEvent event) {
		SettingModel model = setting.getPreference();
		boolean incomingAccept = incomingCheckbox.isSelected();
		long incomingMaximum = (long) incomingSlider.getValue();
		String onDiconnection = onDisconnectCombo.getSelectionModel().getSelectedItem().getText();
		//int timeoutTime = Integer.parseInt( ( (ToggleButton)timeout.getSelectedToggle().getUserData()).getText() );
		Toggle toggle = timeout.getSelectedToggle();
		JFXToggleNode node = (JFXToggleNode) toggle.getUserData();
		String text = node.getId();
		System.out.println(text);
		// TODO
		//SettingModel model2 = new SettingModel(model.getLogFile(), model.getLogDisplay(), );
		//setting.setPreference(model);
    }

    @FXML
    void restoreSettings(ActionEvent event) {
    	setting.restoreDefaults();
    }

	@FXML
	void initialize() throws IOException {
		CacheMan cacheManager = LoginPageController.cacheManager;
		Cache<String, String> userCache = null;
		if(cacheManager != null) {
			userCache = cacheManager.getCacheManager().getCache("user", String.class, String.class);
		}
		if (userCache == null) {
			AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource("/login/view/LoginPage.fxml")); // Change scene
			toBeChanged.setOpacity(1);
			PreLoginPageController.stackPaneClone.getChildren().remove(0);
			PreLoginPageController.stackPaneClone.getChildren().add(0, toBeChanged);
			PreLoginPageController.anchorPaneClone.getChildren().clear();
			PreLoginPageController.navBarClone.setVisible(false);
		} else {
			userCache.put("Last", "/setting/view/SettingPage.fxml");
		}
		
		// Setting
		setting = new Setting();
		SettingModel settingModel = setting.getPreference();

		ArrayList<String> disconnectArray = new ArrayList<String>();
		Collections.addAll(disconnectArray, "Logout", "Signout", "Sleep", "Shutdown");
		for (String s : disconnectArray) {
			onDisconnectCombo.getItems().add(new Label(s));
		}

		int disconnectSetting = settingModel.preProcess(0, settingModel.getOnDiconnection());
		if (disconnectSetting >= 0) {
			onDisconnectCombo.getSelectionModel().select(disconnectSetting);
		} else {
			onDisconnectCombo.getItems().add(new Label("Unexpected error!"));
			onDisconnectCombo.getSelectionModel().selectLast();
		}

		int timeoutSetting = settingModel.preProcess(1, String.valueOf(settingModel.getTimeout()));
		if (timeoutSetting >= 0) {
			switch (timeoutSetting) {
			case 0:
				timeout1.setSelected(true);
				break;
			case 1:
				timeout2.setSelected(true);
				break;
			case 2:
				timeout3.setSelected(true);
				break;
			case 3:
				timeout4.setSelected(true);
				break;
			case 4:
				timeout5.setSelected(true);
				break;
			case 5:
				timeout6.setSelected(true);
				break;
			}
		} else {
			timeout2.setSelected(true);
		}

		incomingCheckbox.setSelected(settingModel.isIncomingAccept());

		File file = new File(".").getAbsoluteFile();
		File root = file.getParentFile();
		while (root.getParentFile() != null) {
			root = root.getParentFile();
		}

		long value = settingModel.getIncomingMaximum();
		long maximum = root.getFreeSpace() / 1024 / 1024; // MB
		incomingSlider.setValue(value);

		SpinnerValueFactory<Integer> valueFactory = new IntegerSpinnerValueFactory(0, 2048, (int) value);
		incomingSliderSpinner.setValueFactory(valueFactory);

		incomingSliderSpinner.valueProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				incomingSlider.setValue(incomingSliderSpinner.getValue());
			}
		});

	}
}
