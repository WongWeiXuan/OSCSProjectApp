package fileStorage;

import java.io.File;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileStorage extends RecursiveTreeObject<FileStorage> {
	private String username;
	private StringProperty fileName;
	private StringProperty fileType;
	private StringProperty dateCreated;
	private File file;
	
	public FileStorage() {
		
	}

	public FileStorage(String fileName, String fileType, String dateCreated) {
		this.fileName = new SimpleStringProperty(fileName);
		this.fileType = new SimpleStringProperty(fileType);
		this.dateCreated = new SimpleStringProperty(dateCreated);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	
	public StringProperty dateCreatedProperty() {
		return this.dateCreated;
	}

	public String getDateCreated() {
		return dateCreated.get();
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated.set(dateCreated);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public static void main(String[] args) {
		
	}

}