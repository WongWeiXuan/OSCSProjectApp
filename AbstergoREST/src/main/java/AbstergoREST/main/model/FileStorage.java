package AbstergoREST.main.model;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileStorage {
	private String username;
	private String fileName;
	private String fileType;
	private Timestamp dateCreated;
	private File file;
	private byte[] splitFile1;
	private byte[] splitFile2;
	private byte[] splitFile3;
	private byte[] splitFile4;
	private byte[] keyBlock;
	private byte[] parBlock;
	
	public FileStorage() {
		
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
	
	public Timestamp getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
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

	public String converTimestamp(Timestamp timestamp) {
		LocalDateTime datetime = timestamp.toLocalDateTime();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
		String dateString = datetime.format(format);
		return dateString;
	}

}
