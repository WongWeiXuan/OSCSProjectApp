package fileBackup;

import java.io.IOException;
import java.util.ArrayList;

import org.ehcache.Cache;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.layout.AnchorPane;
import login.controller.LoginPageController;
import login.controller.PreLoginPageController;

public class DisFileBackupController {
	@FXML
	private JFXTreeTableView<DisFileBackupTreeModel> fileTable;
	
	public static String fileBackupIndex;
	
	Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
	private String username = userCache.get("User");
	
	@FXML
	public void initialize() {
		fileTable.setRowFactory(tv -> {
			TreeTableRow<DisFileBackupTreeModel> row = new TreeTableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					DisFileBackupTreeModel fileItem = row.getItem();
					fileBackupIndex = fileItem.getFileName();
					
					AnchorPane toBeChanged = null;
					try {
						toBeChanged = FXMLLoader.load(getClass().getResource("/fileBackup/FileVerHist.fxml"));
						PreLoginPageController.anchorPaneClone.getChildren().setAll(toBeChanged);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			return row;
		});
		
		JFXTreeTableColumn<DisFileBackupTreeModel, String> nameCol = new JFXTreeTableColumn<>("Name");
		nameCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<DisFileBackupTreeModel, String> param) -> {
			if (nameCol.validateValue(param)) {
				return param.getValue().getValue().fileNameProperty();
			}
		    else {
		    	return nameCol.getComputedValue(param);
		    }
		});
		nameCol.setResizable(false);
		
		JFXTreeTableColumn<DisFileBackupTreeModel, String> typeCol = new JFXTreeTableColumn<>("Type");
		typeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		typeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<DisFileBackupTreeModel, String> param) -> {
			if (typeCol.validateValue(param)) {
				return param.getValue().getValue().fileTypeProperty();
			}
		    else {
		    	return typeCol.getComputedValue(param);
		    }
		});
		typeCol.setResizable(false);
		
		JFXTreeTableColumn<DisFileBackupTreeModel, String> sizeCol = new JFXTreeTableColumn<>("Size");
		sizeCol.prefWidthProperty().bind(fileTable.widthProperty().divide(4));
		sizeCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<DisFileBackupTreeModel, String> param) -> {
			if (sizeCol.validateValue(param)) {
				return param.getValue().getValue().fileSizeProperty();
			}
		    else {
		    	return sizeCol.getComputedValue(param);
		    }
		});
		sizeCol.setResizable(false);
		
		ObservableList<DisFileBackupTreeModel> disFileBackup = FXCollections.observableArrayList();
		ArrayList<DisFileBackup> disFileBackupList = DisFileBackup.getDisFileBackup(username);
		for (DisFileBackup fs : disFileBackupList) {
			disFileBackup.add(new DisFileBackupTreeModel(fs.getFileName(), fs.getFileType(), fs.getFileSize()));
		}
		
		final TreeItem<DisFileBackupTreeModel> root = new RecursiveTreeItem<DisFileBackupTreeModel>(disFileBackup, RecursiveTreeObject::getChildren);
		fileTable.setRoot(root);
		fileTable.setShowRoot(false);
		fileTable.getColumns().add(nameCol);
		fileTable.getColumns().add(typeCol);
		fileTable.getColumns().add(sizeCol);
	}

}
