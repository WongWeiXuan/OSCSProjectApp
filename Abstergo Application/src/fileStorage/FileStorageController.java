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
	private JFXTreeTableView<FileStorage> fileTable;
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
			TreeTableRow<FileStorage> row = new TreeTableRow<>();
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
		
		JFXTreeTableColumn<FileStorage, String> nameCol = new JFXTreeTableColumn<>("Name");
		nameCol.prefWidthProperty().bind(fileTable.widthProperty().divide(2));
		nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorage, String> param) -> {
			if (nameCol.validateValue(param)) {
				return param.getValue().getValue().nameProperty();
			}
		    else {
		    	return nameCol.getComputedValue(param);
		    }
		});
		nameCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorage, String> typeCol = new JFXTreeTableColumn<>("Type");
		typeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorage, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().typeProperty();
			}
		    else {
		    	return typeCol.getComputedValue(param);
		    }
		});
		typeCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorage, String> dateCol = new JFXTreeTableColumn<>("Date Created");
		dateCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		dateCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorage, String> param) -> {
			if (dateCol.validateValue(param)) {
				return param.getValue().getValue().dateCreatedProperty();
			}
		    else {
		    	return dateCol.getComputedValue(param);
		    }
		});
		dateCol.setResizable(false);
		
		ObservableList<FileStorage> fileStorage = FXCollections.observableArrayList();
		fileStorage.add(new FileStorage("A certain word document", "Document", "23/5/2018"));
		fileStorage.add(new FileStorage("Hello World", "Document", "24/5/2018"));
		fileStorage.add(new FileStorage("World Hello", "Document", "25/5/2018"));
		fileStorage.add(new FileStorage("The fourth one", "Document", "26/5/2018"));
		fileStorage.add(new FileStorage("Fifth", "Document", "27/5/2018"));
		
		final TreeItem<FileStorage> root = new RecursiveTreeItem<FileStorage>(fileStorage, RecursiveTreeObject::getChildren);
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