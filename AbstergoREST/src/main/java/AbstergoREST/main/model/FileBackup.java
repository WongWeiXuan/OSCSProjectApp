package AbstergoREST.main.model;

public class FileBackup {
	private String username;
	private String fileName;
	private String fileType;
	private String fileSize;
	private byte[] fileData;
	private String encKey;
	private String dateCreated;
	private String fileBackupIndex;
	
	public FileBackup() {
		super();
	}

	public FileBackup(String username, String fileName, String fileType, byte[] fileData, String encKey, String dateCreated, String fileBackupIndex) {
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

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
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
}
