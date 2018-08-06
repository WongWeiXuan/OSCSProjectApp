package AbstergoREST.main.model;

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
}
