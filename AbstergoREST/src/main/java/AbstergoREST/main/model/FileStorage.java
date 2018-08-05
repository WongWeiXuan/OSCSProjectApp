package AbstergoREST.main.model;

public class FileStorage {
	private String username;
	private String fileName;
	private String fileType;
	private String fileSize;
	private String dateCreated;
	private byte[] file;
	private byte[] splitFile1;
	private byte[] splitFile2;
	private byte[] splitFile3;
	private byte[] splitFile4;
	private byte[] keyBlock;
	private byte[] parBlock;
	private int noOfFiles;
	
	public FileStorage() {
		
	}
	
	public FileStorage(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public FileStorage(String username, String fileName, String fileType, String fileSize, String dateCreated, byte[] splitFile1,
			byte[] splitFile2, byte[] splitFile3, byte[] splitFile4, byte[] keyBlock, byte[] parBlock, int noOfFiles) {
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
	
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public byte[] getSplitFile1() {
		return splitFile1;
	}
	
	public void setSplitFile1(byte[] splitFile1) {
		this.splitFile1 = splitFile1;
	}
	
	public byte[] getSplitFile2() {
		return splitFile2;
	}
	
	public void setSplitFile2(byte[] splitFile2) {
		this.splitFile2 = splitFile2;
	}
	
	public byte[] getSplitFile3() {
		return splitFile3;
	}
	
	public void setSplitFile3(byte[] splitFile3) {
		this.splitFile3 = splitFile3;
	}
	
	public byte[] getSplitFile4() {
		return splitFile4;
	}
	
	public void setSplitFile4(byte[] splitFile4) {
		this.splitFile4 = splitFile4;
	}
	
	public byte[] getKeyBlock() {
		return keyBlock;
	}

	public void setKeyBlock(byte[] keyBlock) {
		this.keyBlock = keyBlock;
	}

	public byte[] getParBlock() {
		return parBlock;
	}

	public void setParBlock(byte[] parBlock) {
		this.parBlock = parBlock;
	}

	public int getNoOfFiles() {
		return noOfFiles;
	}

	public void setNoOfFiles(int noOfFiles) {
		this.noOfFiles = noOfFiles;
	}
}
