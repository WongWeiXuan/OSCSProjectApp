package logExtra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Transcation {
	private String userFrom;
	private String userTo;
	private long time;
	private String fileHash;
	// Add more pls

	public Transcation(String userFrom, String userTo, File file) {
		this.userFrom = userFrom;
		this.userTo = userTo;
		this.time = new Date().getTime();
		this.fileHash = generateSHA(file);
	}

	public static String generateSHA(File file) {
		FileInputStream fis = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int bytesCount = 0;

			while ((bytesCount = fis.read(buffer)) != -1) {
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
}
