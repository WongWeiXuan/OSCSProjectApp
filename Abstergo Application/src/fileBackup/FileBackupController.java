package fileBackup;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fileStorage.FileSecure;
import fileStorage.FileStorage;
import fileStorage.FileStorageTreeModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.controller.LoginPageController;

public class FileBackupController {
	@FXML
	private VBox backupWrap;
	@FXML
	private JFXButton backupBtn;
	@FXML
	private JFXTreeTableView<FileStorageTreeModel> fileTable;
	
	private String fileToBackup;
	
	public static String confirmMsg;
	
	Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
	private String username = userCache.get("User");
	
	@FXML
	public void initialize() throws UnsupportedEncodingException {
		fileTable.setRowFactory(tv -> {
			TreeTableRow<FileStorageTreeModel> row = new TreeTableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					backupWrap.setVisible(true);
					backupWrap.setDisable(false);
				}
				else {
					backupWrap.setVisible(false);
					backupWrap.setDisable(true);
					fileTable.getSelectionModel().clearSelection();
				}
				
				FileStorageTreeModel fileItem = row.getItem();
				fileToBackup = fileItem.getFileName();
			});
			return row;
		});
		
		JFXTreeTableColumn<FileStorageTreeModel, String> nameCol = new JFXTreeTableColumn<>("Name");
		nameCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageTreeModel, String> param) -> {
			if (nameCol.validateValue(param)) {
				return param.getValue().getValue().fileNameProperty();
			}
		    else {
		    	return nameCol.getComputedValue(param);
		    }
		});
		nameCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorageTreeModel, String> typeCol = new JFXTreeTableColumn<>("Type");
		typeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageTreeModel, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().fileTypeProperty();
			}
		    else {
		    	return typeCol.getComputedValue(param);
		    }
		});
		typeCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorageTreeModel, String> sizeCol = new JFXTreeTableColumn<>("Size");
		sizeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		sizeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageTreeModel, String> param) -> {
			if (sizeCol.validateValue(param)) {
				return param.getValue().getValue().fileSizeProperty();
			}
		    else {
		    	return sizeCol.getComputedValue(param);
		    }
		});
		sizeCol.setResizable(false);
		
		JFXTreeTableColumn<FileStorageTreeModel, String> dateCol = new JFXTreeTableColumn<>("Date Created");
		dateCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		dateCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileStorageTreeModel, String> param) -> {
			if (dateCol.validateValue(param)) {
				return param.getValue().getValue().dateCreatedProperty();
			}
		    else {
		    	return dateCol.getComputedValue(param);
		    }
		});
		dateCol.setResizable(false);
		
		ObservableList<FileStorageTreeModel> fileStorage = FXCollections.observableArrayList();
		ArrayList<FileStorage> userFileList = FileStorage.getUserFiles(username);
		for (FileStorage fs : userFileList) {
			fileStorage.add(new FileStorageTreeModel(fs.getFileName(), fs.getFileType(), fs.getFileSize(), fs.getDateCreated()));
		}
		
		final TreeItem<FileStorageTreeModel> root = new RecursiveTreeItem<FileStorageTreeModel>(fileStorage, RecursiveTreeObject::getChildren);
		fileTable.setRoot(root);
		fileTable.setShowRoot(false);
		fileTable.getColumns().add(nameCol);
		fileTable.getColumns().add(typeCol);
		fileTable.getColumns().add(sizeCol);
		fileTable.getColumns().add(dateCol);
	}

	@FXML
	public void backupFile(ActionEvent event) throws IOException, Exception {
		if (!fileToBackup.equals(null)) {
			String encKey = FileSecure.generateEncKey();
			FileBackup fb = BackupFile.mergeFiles(BackupFile.listOfFilesToMerge(username, fileToBackup), new File(System.getProperty("user.home") + "/Downloads/" + fileToBackup), username, encKey);
			FileBackup.backupFile(fb);
			
			DisFileBackup dfb = new DisFileBackup();
			dfb.setUsername(username);
			dfb.setFileName(fileToBackup);
			dfb.setFileType(fb.getFileType());
			dfb.setFileSize(fb.getFileSize());
			DisFileBackup.insertDisFileBackup(dfb);
			
			confirmMsg = "File backed up successfully!";
			
			Dialog<?> dialog = new Dialog<Object>();
			Parent root = FXMLLoader.load(getClass().getResource("/fileBackup/FileBackupConfirm.fxml"));
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.initStyle(StageStyle.TRANSPARENT);
			Scene scene = new Scene(root);
			stage.setX(800);
			stage.setY(400);
			stage.setScene(scene);
			stage.showAndWait();
		}
	}
}
