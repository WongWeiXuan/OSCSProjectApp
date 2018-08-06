package fileBackup;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DisFileBackup {
	private String username;
	private String fileName;
	private String fileType;
	private String fileSize;
	
	public DisFileBackup() {
		super();
	}

	public DisFileBackup(String username, String fileName, String fileType, String fileSize) {
		super();
		this.username = username;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
	public static void insertDisFileBackup(DisFileBackup dfb) {
		DisFileBackupDAO.insertDisFileBackup(dfb);
	}
	
	public static ArrayList<DisFileBackup> getDisFileBackup(String username) {
		ArrayList<DisFileBackup> disFileBackupList = new ArrayList<DisFileBackup>();
		JsonArray jsonArray = DisFileBackupDAO.getDisFileBackup(username);
		if (jsonArray != null ) {
			for (JsonElement je : jsonArray) {
				JsonObject jsonObject = je.getAsJsonObject();
				DisFileBackup dfb = new DisFileBackup();
				dfb.setUsername(jsonObject.get("username").getAsString());
				dfb.setFileName(jsonObject.get("fileName").getAsString());
				dfb.setFileType(jsonObject.get("fileType").getAsString());
				dfb.setFileSize(jsonObject.get("fileSize").getAsString());
			}
		}
		return disFileBackupList;
	}
}
