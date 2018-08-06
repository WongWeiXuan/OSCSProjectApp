package fileBackup;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DisFileBackupTreeModel extends RecursiveTreeObject<DisFileBackupTreeModel> {
	private StringProperty fileName;
	private StringProperty fileType;
	private StringProperty fileSize;
	
	public DisFileBackupTreeModel() {
		
	}
	
	public DisFileBackupTreeModel(String fileName, String fileType, String fileSize) {
		this.fileName = new SimpleStringProperty(fileName);
		this.fileType = new SimpleStringProperty(fileType);
		this.fileSize = new SimpleStringProperty(fileSize);
	}
	
	public StringProperty fileNameProperty() {
		return this.fileName;
	}
	
	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(String fileName) {
		this.fileName = new SimpleStringProperty(fileName);
	}
	
	public StringProperty fileTypeProperty() {
		return this.fileType;
	}

	public String getFileType() {
		return fileType.get();
	}

	public void setFileType(String fileType) {
		this.fileType = new SimpleStringProperty(fileType);
	}
	
	public StringProperty fileSizeProperty() {
		return this.fileSize;
	}
	
	public String getFileSize() {
		return fileSize.get();
	}
	
	public void setFileSize(String fileSize) {
		this.fileSize = new SimpleStringProperty(fileSize);
	}
}
