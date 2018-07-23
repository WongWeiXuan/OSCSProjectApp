package logExtra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Transcation {
	private String userFrom;
	private String userTo;
	private long time;
	private String fileHash;
	private boolean broadcastFile;

	public Transcation(String userFrom, String userTo, File file) {
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.time = new Date().getTime();
		this.fileHash = generateSHA(file);
	}
	
	public Transcation(String userFrom, String userTo, File file, boolean broadcastFile) {
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.time = new Date().getTime();
		this.fileHash = generateSHA(file);
		this.broadcastFile = broadcastFile;
	}
	
	public Transcation(String userFrom, String userTo, long time, String fileHash, boolean broadcastFile) {
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.time = time;
		this.fileHash = fileHash;
		this.broadcastFile = broadcastFile;
	}

	public static String generateSHA(File file) {
		FileInputStream fis = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			fis = new FileInputStream(file);
			DigestInputStream dis = new DigestInputStream(fis, digest);
			byte[] buffer = new byte[1024];
			int bytesCount = 0;

			while ((bytesCount = dis.read(buffer)) != -1) {
				digest.update(buffer, 0, bytesCount);
			}

			fis.close();

			byte[] bytes = digest.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public String getUserFrom() {
		return userFrom;
	}

	public String getUserTo() {
		return userTo;
	}

	protected long getTime() {
		return time;
	}

	protected String getFileHash() {
		return fileHash;
	}

	protected boolean isBroadcastFile() {
		return broadcastFile;
	}
}
