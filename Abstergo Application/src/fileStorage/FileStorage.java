package fileStorage;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileStorage extends RecursiveTreeObject<FileStorage> {
	private StringProperty name;
	private StringProperty type;
	private StringProperty dateCreated;
	
	public FileStorage() {
		
	}

	public FileStorage(String name, String type, String dateCreated) {
		this.name = new SimpleStringProperty(name);
		this.type = new SimpleStringProperty(type);
		this.dateCreated = new SimpleStringProperty(dateCreated);
	}
	
	public StringProperty nameProperty() {
		return this.name;
	}
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty typeProperty() {
		return this.type;
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
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