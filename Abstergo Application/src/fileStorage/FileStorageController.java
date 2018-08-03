package fileStorage;

import java.io.File;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class FileStorageController {
	@FXML
	private JFXTreeTableView<FileStorageModel> fileTable;
	@FXML
	private HBox tableWrap;
	@FXML
	private VBox newWrap;
	@FXML
	private JFXButton newBtn;
	@FXML
	private VBox newList;
	@FXML
	private Label fileUploadOpt;
	@FXML
	private VBox downloadWrap;
	@FXML
	private JFXButton downloadBtn;
	@FXML
	private Label historyOpt;
	@FXML
	private Label moveOpt;
	@FXML
	private Label copyOpt;
	@FXML
	private Label deleteOpt;
	
	@FXML
	public void initialize() {
		fileTable.setRowFactory(tv -> {
			TreeTableRow<FileStorageModel> row = new TreeTableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					newWrap.setVisible(false);
					newWrap.setDisable(true);
					downloadWrap.setVisible(true);
					downloadWrap.setDisable(false);
				}
				else {
					newWrap.setVisible(true);
					newWrap.setDisable(false);
					downloadWrap.setVisible(false);
					downloadWrap.setDisable(true);
					fileTable.getSelectionModel().clearSelection();
				}
			});
			return row;
		});
		
		JFXTreeTableColumn<FileStorageModel, String> nameCol = new JFXTreeTableColumn<>("Name");
		nameCol.prefWidthProperty().bind(fileTable.widthProperty().divide(2));
		nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageModel, String> param) -> {
			if (nameCol.validateValue(param)) {
				return param.getValue().getValue().fileNameProperty();
			}
		    else {
		    	return nameCol.getComputedValue(param);
		    }
		});
		nameCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorageModel, String> typeCol = new JFXTreeTableColumn<>("Type");
		typeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageModel, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().fileTypeProperty();
			}
		    else {
		    	return typeCol.getComputedValue(param);
		    }
		});
		typeCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorageModel, String> dateCol = new JFXTreeTableColumn<>("Date Created");
		dateCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		dateCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageModel, String> param) -> {
			if (dateCol.validateValue(param)) {
				return param.getValue().getValue().dateCreatedProperty();
			}
		    else {
		    	return dateCol.getComputedValue(param);
		    }
		});
		dateCol.setResizable(false);
		
		ObservableList<FileStorageModel> fileStorage = FXCollections.observableArrayList();
		
		final TreeItem<FileStorageModel> root = new RecursiveTreeItem<FileStorageModel>(fileStorage, RecursiveTreeObject::getChildren);
		fileTable.setRoot(root);
		fileTable.setShowRoot(false);
		fileTable.getColumns().add(nameCol);
		fileTable.getColumns().add(typeCol);
		fileTable.getColumns().add(dateCol);
	}
	
	@FXML
    void showNewBtn(ActionEvent event) {
		if (newList.isVisible()) {
			newList.setVisible(false);
			newList.setDisable(true);
		}
		else {
			newList.setVisible(true);
			newList.setDisable(false);
		}
    }
	
	@FXML
    void fileUpload(MouseEvent event) {
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File("C:"));
		File selectedFile = fc.showOpenDialog(null);
    }

}