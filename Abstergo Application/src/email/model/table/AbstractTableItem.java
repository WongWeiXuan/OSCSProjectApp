package email.model.table;

import javafx.beans.property.SimpleBooleanProperty;

public abstract class AbstractTableItem {
	
	private final SimpleBooleanProperty read = new SimpleBooleanProperty();
	private final SimpleBooleanProperty encrypt = new SimpleBooleanProperty();
	
	public AbstractTableItem (boolean isRead){
		this.setRead(isRead);
		//this.setRead(encryptt);
	}
	
	public SimpleBooleanProperty getReadProperty(){
		return read;
	}
	
	/*
	public SimpleBooleanProperty getEncryptProperty() {
		return encrypt;
	}
	*/
	public void setRead(boolean isRead){
		read.set(isRead);
	}
	public boolean isRead(){
		return read.get();
	}
	/*
	public void setencrypt(boolean encryptt) {
		encrypt.set(encryptt);
	}
	
	public boolean isEncrypt() {
		return encrypt.get();
	}
	*/

}
