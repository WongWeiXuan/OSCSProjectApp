package AbstergoREST.main.model;

public class FileHash {
	private String fileName;
	private String fileSHA1;
	
	public FileHash() {}
	
	public FileHash(String fileName, String fileSHA1) {
		this.fileName = fileName;
		this.fileSHA1 = fileSHA1;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSHA1() {
		return fileSHA1;
	}

	public void setFileSHA1(String fileSHA1) {
		this.fileSHA1 = fileSHA1;
	}
}
