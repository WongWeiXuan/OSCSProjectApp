package log.controller;

import java.util.function.Predicate;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawer.DrawerDirection;
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
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import log.LogDetailsTree;
import log.LogModelModel;

public class LogPageController {
	// Navigation Bar
	@FXML
	private JFXDrawer jfxDrawer;
	@FXML
	private VBox drawerVBox;
	@FXML
	private ImageView logoImageview;
	@FXML
	private Label emailNav;
	@FXML
	private Label settingsNav;

	@FXML
	void toggleDrawer(MouseEvent event) {
		jfxDrawer.toggle();
	}

	// Logs
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

	@FXML
	void initialize() {
		// Navigation
		jfxDrawer.setSidePane(drawerVBox);
		jfxDrawer.setDefaultDrawerSize(120);
		jfxDrawer.setOverLayVisible(true);
		jfxDrawer.setDirection(DrawerDirection.RIGHT);
		jfxDrawer.close();

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
