package fileStorage;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileStorageTreeModel extends RecursiveTreeObject<FileStorageTreeModel> {
	private StringProperty fileName;
	private StringProperty fileType;
	private StringProperty fileSize;
	private StringProperty dateCreated;
	
	public FileStorageTreeModel() {
		
	}

	public FileStorageTreeModel(String fileName, String fileType, String fileSize, String dateCreated) {
		this.fileName = new SimpleStringProperty(fileName);
		this.fileType = new SimpleStringProperty(fileType);
		this.fileSize = new SimpleStringProperty(fileSize);
		this.dateCreated = new SimpleStringProperty(dateCreated);
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
	
	public StringProperty dateCreatedProperty() {
		return this.dateCreated;
	}

	public String getDateCreated() {
		return dateCreated.get();
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated.set(dateCreated);
	}

	public static void main(String[] args) {
		
	}

}