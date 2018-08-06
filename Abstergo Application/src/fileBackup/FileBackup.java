package fileBackup;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FileBackup {
	private String username;
	private String fileName;
	private String fileType;
	private String fileSize;
	private String fileData;
	private String encKey;
	private String dateCreated;
	private String fileBackupIndex;
	
	public FileBackup() {
		super();
	}
	
	public FileBackup(String username, String fileName, String fileType, String dateCreated) {
		super();
		this.username = username;
		this.fileName = fileName;
		this.fileType = fileType;
		this.dateCreated = dateCreated;
	}

	public FileBackup(String username, String fileName, String fileType, String fileData, String encKey, String dateCreated, String fileBackupIndex) {
		super();
		this.username = username;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileData = fileData;
		this.encKey = encKey;
		this.dateCreated = dateCreated;
		this.fileBackupIndex = fileBackupIndex;
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

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public String getEncKey() {
		return encKey;
	}

	public void setEncKey(String encKey) {
		this.encKey = encKey;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getFileBackupIndex() {
		return fileBackupIndex;
	}

	public void setFileBackupIndex(String fileBackupIndex) {
		this.fileBackupIndex = fileBackupIndex;
	}
	
	public static String convertTimestamp(Timestamp timestamp) {
		LocalDateTime datetime = timestamp.toLocalDateTime();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		String dateString = datetime.format(format);
		return dateString;
	}
	
	public static ArrayList<FileBackup> getBackupFileNames(String username) {
		ArrayList<FileBackup> backupFileList = new ArrayList<FileBackup>();
		JsonArray jsonArray = FileBackupDAO.getBackupFileNames(username);
		if (jsonArray != null) {
			for (JsonElement je : jsonArray) {
				JsonObject jsonObject = je.getAsJsonObject();
				FileBackup fb = new FileBackup();
				fb.setFileName(jsonObject.get("fileName").getAsString());
				backupFileList.add(fb);
			}
		}
		return backupFileList;
	}
	
	public static ArrayList<FileBackup> getFileVerHist(String username, String fileBackupIndex) {
		ArrayList<FileBackup> backupFileList = new ArrayList<FileBackup>();
		JsonArray jsonArray = FileBackupDAO.getFileVerHist(username, fileBackupIndex);
		if (jsonArray != null) {
			for (JsonElement je : jsonArray) {
				JsonObject jsonObject = je.getAsJsonObject();
				FileBackup fb = new FileBackup();
				fb.setUsername(jsonObject.get("username").getAsString());
				fb.setFileName(jsonObject.get("fileName").getAsString());
				fb.setFileType(jsonObject.get("fileType").getAsString());
				fb.setDateCreated(jsonObject.get("dateCreated").getAsString());
				backupFileList.add(fb);
			}
		}
		return backupFileList;
	}
	
	public static void backupFile(FileBackup fb) {
		FileBackupDAO.backupFile(fb);
	}
	
	public static FileBackup downloadBackupFile(String username, String fileName) {
		JsonObject jsonObject = FileBackupDAO.downloadBackupFile(username, fileName);
		FileBackup fb = new FileBackup();
		fb.setUsername(jsonObject.get("username").getAsString());
		fb.setFileName(jsonObject.get("fileName").getAsString());
		fb.setFileType(jsonObject.get("fileType").getAsString());
		fb.setEncKey(jsonObject.get("encKey").getAsString());
		fb.setFileData(jsonObject.get("fileData").getAsString());
		fb.setDateCreated(jsonObject.get("dateCreated").getAsString());
		fb.setFileBackupIndex(jsonObject.get("fileBackupIndex").getAsString());
		return fb;
	}
}
