package log.controller;

import java.io.IOException;
import java.util.function.Predicate;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import log.LogDetailsTree;
import log.LogModelModel;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;

public class LogPageController {
	// Logs
	@FXML
    private AnchorPane root;
	@FXML
	private JFXButton updateBtn;
	@FXML
	private JFXButton downloadBtn;
	@FXML
	private JFXButton filterBtn;
	@FXML
	private JFXTreeTableView<LogDetailsTree> logTable;
	@FXML
    private JFXComboBox<Label> filterCombo;
    @FXML
    private JFXTextField filterInput;

	@FXML
	void establishFileTransfer(ActionEvent event) {

	}

	@FXML
	void filterLogs(ActionEvent event) {

	}

	@FXML
	void updateAndGetLogs(ActionEvent event) {

	}

	@SuppressWarnings("unchecked")
	@FXML
	void initialize() throws IOException {
		Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
		if (userCache == null) {
			AnchorPane toBeChanged = FXMLLoader.load(getClass().getResource("/login/view/Login.fxml")); // Change scene
			toBeChanged.setOpacity(1);
			PreLoginPageController.stackPaneClone.getChildren().remove(0);
			PreLoginPageController.stackPaneClone.getChildren().add(0, toBeChanged);
			PreLoginPageController.anchorPaneClone.getChildren().clear();
			PreLoginPageController.navBarClone.setVisible(false);
		} else {
			userCache.put("Last", "/setting/view/SettingPage.fxml");
		}

		// Logs
		LogModelModel model = new LogModelModel();
		
		JFXTreeTableColumn<LogDetailsTree, String> eventCategory = new JFXTreeTableColumn<LogDetailsTree, String>("Event Category");
		eventCategory.setPrefWidth(150);
		eventCategory.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventCategory();
			}
			
		});
		
		JFXTreeTableColumn<LogDetailsTree, String> eventID = new JFXTreeTableColumn<LogDetailsTree, String>("Event ID");
		eventID.setPrefWidth(150);
		eventID.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventID();
			}
			
		});
		
		JFXTreeTableColumn<LogDetailsTree, String> eventSource = new JFXTreeTableColumn<LogDetailsTree, String>("Event Source");
		eventSource.setPrefWidth(150);
		eventSource.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventSource();
			}
			
		});

		JFXTreeTableColumn<LogDetailsTree, String> eventType = new JFXTreeTableColumn<LogDetailsTree, String>("Event Type");
		eventType.setPrefWidth(150);
		eventType.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventType();
			}
			
		});
		
		JFXTreeTableColumn<LogDetailsTree, String> recordNumber = new JFXTreeTableColumn<LogDetailsTree, String>("Record Number");
		recordNumber.setPrefWidth(150);
		recordNumber.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getRecordNumber();
			}
			
		});
		
		JFXTreeTableColumn<LogDetailsTree, String> eventTimeGenerated = new JFXTreeTableColumn<LogDetailsTree, String>("Event Time Generated");
		eventTimeGenerated.setPrefWidth(150);
		eventTimeGenerated.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventTimeGenerated();
			}
			
		});
		
		JFXTreeTableColumn<LogDetailsTree, String> eventTimeWritten = new JFXTreeTableColumn<LogDetailsTree, String>("Event Time Written");
		eventTimeWritten.setPrefWidth(150);
		eventTimeWritten.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventTimeWritten();
			}
			
		});
		
		JFXTreeTableColumn<LogDetailsTree, String> eventData = new JFXTreeTableColumn<LogDetailsTree, String>("Event Data");
		eventData.setPrefWidth(150);
		eventData.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LogDetailsTree, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<LogDetailsTree, String> param) {
				return param.getValue().getValue().getEventData();
			}
			
		});
		
		ObservableList<LogDetailsTree> logDetailsTree = FXCollections.observableArrayList();
		logDetailsTree.addAll(model.getLogsDetails(0));
		
		final TreeItem<LogDetailsTree> root = new RecursiveTreeItem<LogDetailsTree>(logDetailsTree, RecursiveTreeObject::getChildren);
		logTable.getColumns().setAll(recordNumber, eventType, eventID, eventTimeGenerated, eventData, eventCategory, eventSource, eventTimeWritten);
		logTable.setRoot(root);
		logTable.setShowRoot(false);
		
		filterInput.textProperty().addListener(new ChangeListener<String>() {
			
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				logTable.setPredicate(new Predicate<TreeItem<LogDetailsTree>>() {
					@Override
					public boolean test(TreeItem<LogDetailsTree> t) {
//						String value = filterCombo.getSelectionModel().getSelectedItem().getText();
						Boolean returnValue = t.getValue().getRecordNumber().getValue().contains(newValue);
						return returnValue;
					}
					
				});
			}
		});
	}
}
