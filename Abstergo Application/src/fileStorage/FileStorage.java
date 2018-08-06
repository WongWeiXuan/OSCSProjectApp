package fileStorage;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FileStorage {
	private String username;
	private String fileName;
	private String fileType;
	private String fileSize;
	private String dateCreated;
	private String file;
	private String splitFile1;
	private String splitFile2;
	private String splitFile3;
	private String splitFile4;
	private String keyBlock;
	private String parBlock;
	private int noOfFiles;
	
	public FileStorage() {
		
	}
	
	public FileStorage(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public FileStorage(String username, String fileName, String fileType, String fileSize, String dateCreated, String splitFile1,
			String splitFile2, String splitFile3, String splitFile4, String keyBlock, String parBlock, int noOfFiles) {
		super();
		this.username = username;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.dateCreated = dateCreated;
		this.splitFile1 = splitFile1;
		this.splitFile2 = splitFile2;
		this.splitFile3 = splitFile3;
		this.splitFile4 = splitFile4;
		this.keyBlock = keyBlock;
		this.parBlock = parBlock;
		this.noOfFiles = noOfFiles;
	}
	
	public FileStorage(String username, String fileName, String fileType, String fileSize, String dateCreated) {
		super();
		this.username = username;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.dateCreated = dateCreated;
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

	public String getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getSplitFile1() {
		return splitFile1;
	}
	
	public void setSplitFile1(String splitFile1) {
		this.splitFile1 = splitFile1;
	}
	
	public String getSplitFile2() {
		return splitFile2;
	}
	
	public void setSplitFile2(String splitFile2) {
		this.splitFile2 = splitFile2;
	}
	
	public String getSplitFile3() {
		return splitFile3;
	}
	
	public void setSplitFile3(String splitFile3) {
		this.splitFile3 = splitFile3;
	}
	
	public String getSplitFile4() {
		return splitFile4;
	}
	
	public void setSplitFile4(String splitFile4) {
		this.splitFile4 = splitFile4;
	}
	
	public String getKeyBlock() {
		return keyBlock;
	}

	public void setKeyBlock(String keyBlock) {
		this.keyBlock = keyBlock;
	}

	public String getParBlock() {
		return parBlock;
	}

	public void setParBlock(String parBlock) {
		this.parBlock = parBlock;
	}

	public int getNoOfFiles() {
		return noOfFiles;
	}

	public void setNoOfFiles(int noOfFiles) {
		this.noOfFiles = noOfFiles;
	}

	public static String convertTimestamp(Timestamp timestamp) {
		LocalDateTime datetime = timestamp.toLocalDateTime();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		String dateString = datetime.format(format);
		return dateString;
	}
	
	public static ArrayList<FileStorage> getUserFiles(String username) throws UnsupportedEncodingException {
		ArrayList<FileStorage> userFileList = new ArrayList<FileStorage>();
		JsonArray jsonArray = FileStorageDAO.getUserFiles(username);
		if (jsonArray != null) {
			for (JsonElement je : jsonArray) {
				JsonObject jsonObject = je.getAsJsonObject();
				FileStorage fs = new FileStorage();
				fs.setUsername(jsonObject.get("username").getAsString());
				fs.setFileName(jsonObject.get("fileName").getAsString());
				fs.setFileSize(jsonObject.get("fileSize").getAsString());
				fs.setFileType(jsonObject.get("fileType").getAsString());
				fs.setDateCreated(jsonObject.get("dateCreated").getAsString());
				userFileList.add(fs);
			}
		}
		return userFileList;
	}
	
	public static void uploadFile(FileStorage fs) {
		FileStorageDAO.uploadFile(fs);
	}
	
	public static FileStorage getSplitFiles(String username, String fileName) throws UnsupportedEncodingException {
		JsonObject jsonObject = FileStorageDAO.getSplitFiles(username, fileName);
		FileStorage fs = new FileStorage();
		fs.setUsername(jsonObject.get("username").getAsString());
		fs.setFileName(jsonObject.get("fileName").getAsString());
		fs.setFileType(jsonObject.get("fileType").getAsString());
		fs.setDateCreated(jsonObject.get("dateCreated").getAsString());
		fs.setSplitFile1(jsonObject.get("splitFile1").getAsString());
		fs.setSplitFile2(jsonObject.get("splitFile2").getAsString());
		fs.setSplitFile3(jsonObject.get("splitFile3").getAsString());
		if (jsonObject.get("splitFile4").isJsonNull()) {
			fs.setSplitFile4("Placeholder");
		}
		else {
			fs.setSplitFile4(jsonObject.get("splitFile4").getAsString());
		}
		fs.setKeyBlock(jsonObject.get("keyBlock").getAsString());
		fs.setParBlock(jsonObject.get("parBlock").getAsString());
		fs.setNoOfFiles(jsonObject.get("noOfFiles").getAsInt());
		
		return fs;
	}
	
	public static void writeMissingFile(FileStorage fs, int missingFileNo) {
		FileStorageDAO.writeMissingFile(fs, missingFileNo);
	}
	
	public static void deleteFile(FileStorage fs) {
		FileStorageDAO.deleteFile(fs);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
	}

}
