package fileBackup;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.ehcache.Cache;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

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

public class FileVerHistController {
	@FXML
	private VBox recoverWrap;
	@FXML
	private JFXButton recoverBtn;
	@FXML
	private JFXTreeTableView<FileBackupTreeModel> fileTable;
	
	private String fileToRecover;
	
	Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
	private String username = userCache.get("User");
	
	@FXML
	public void initialize() throws UnsupportedEncodingException {
		fileTable.setRowFactory(tv -> {
			TreeTableRow<FileBackupTreeModel> row = new TreeTableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					recoverWrap.setVisible(true);
					recoverWrap.setDisable(false);
				}
				else {
					recoverWrap.setVisible(false);
					recoverWrap.setDisable(true);
					fileTable.getSelectionModel().clearSelection();
				}
				
				FileBackupTreeModel fileItem = row.getItem();
				fileToRecover = fileItem.getFileName();
			});
			return row;
		});
		
		JFXTreeTableColumn<FileBackupTreeModel, String> nameCol = new JFXTreeTableColumn<>("Name");
		nameCol.prefWidthProperty().bind(fileTable.widthProperty().divide(2));
		nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileBackupTreeModel, String> param) -> {
			if (nameCol.validateValue(param)) {
				return param.getValue().getValue().fileNameProperty();
			}
		    else {
		    	return nameCol.getComputedValue(param);
		    }
		});
		nameCol.setResizable(false);
		
		JFXTreeTableColumn<FileBackupTreeModel, String> typeCol = new JFXTreeTableColumn<>("Type");
		typeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileBackupTreeModel, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().fileTypeProperty();
			}
		    else {
		    	return typeCol.getComputedValue(param);
		    }
		});
		typeCol.setResizable(false);
		
		JFXTreeTableColumn<FileBackupTreeModel, String> dateCol = new JFXTreeTableColumn<>("Date Created");
		dateCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		dateCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<FileBackupTreeModel, String> param) -> {
			if (dateCol.validateValue(param)) {
				return param.getValue().getValue().dateCreatedProperty();
			}
		    else {
		    	return dateCol.getComputedValue(param);
		    }
		});
		dateCol.setResizable(false);
		
		ObservableList<FileBackupTreeModel> fileBackup = FXCollections.observableArrayList();
		ArrayList<FileBackup> backupFileList = FileBackup.getFileVerHist(username, DisFileBackupController.fileBackupIndex);
		for (FileBackup fs : backupFileList) {
			fileBackup.add(new FileBackupTreeModel(fs.getFileName(), fs.getFileType(), fs.getDateCreated()));
		}
		
		final TreeItem<FileBackupTreeModel> root = new RecursiveTreeItem<FileBackupTreeModel>(fileBackup, RecursiveTreeObject::getChildren);
		fileTable.setRoot(root);
		fileTable.setShowRoot(false);
		fileTable.getColumns().add(nameCol);
		fileTable.getColumns().add(typeCol);
		fileTable.getColumns().add(dateCol);
	}

	@FXML
	public void recoverFile(ActionEvent event) throws Exception {
		FileBackup fb = FileBackup.downloadBackupFile(username, fileToRecover);
		FileUtils.writeByteArrayToFile(new File(System.getProperty("user.home") + "/Downloads/" + fileToRecover), BackupFile.recoverFile(fb.getFileData(), fb.getEncKey()));
		
		FileBackupController.confirmMsg = "File recovered successfully!";
		
		Dialog dialog = new Dialog();
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
