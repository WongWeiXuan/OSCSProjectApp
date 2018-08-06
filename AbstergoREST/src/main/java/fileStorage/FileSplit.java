package fileStorage;

import java.util.Arrays;
import java.util.Base64;

import AbstergoREST.main.database.Database;

public class FileSplit {

	public static byte[][] splitBytes(final byte[] data, final int chunkSize) {
		final int length = data.length;
		final byte[][] dest = new byte[(length + chunkSize - 1) / chunkSize][];
		int destIndex = 0;
		int stopIndex = 0;

		for (int startIndex = 0; startIndex + chunkSize <= length; startIndex += chunkSize) {
			stopIndex += chunkSize;
			dest[destIndex++] = Arrays.copyOfRange(data, startIndex, stopIndex);
		}

		if (stopIndex < length)
			dest[destIndex] = Arrays.copyOfRange(data, stopIndex, length);

		return dest;
	}

	public static void splitFile(String username, String fileName, String fileType, String fileSize, String dateCreated, byte[] file) throws Exception {
		byte[][] arrByteArr = splitBytes(file, file.length / 3);
		
		String encKey = FileSecure.generateEncKey();

		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		byte[] alpha = null, beta = null, charlie = null, delta = null;

		for (byte[] b : arrByteArr) {
			byte[] block = FileSecure.encrypt(b, encKey);
			
			if (b1 == null) {
				b1 = FileSecure.hash(block);
			}
			else if (b2 == null) {
				b2 = FileSecure.hash(block);
			}
			else if (b3 == null) {
				b3 = FileSecure.hash(block);
			}
			else if (b4 == null) {
				b4 = FileSecure.hash(block);
			}
			
			if (alpha == null) {
				alpha = block;
			}
			else if (beta == null) {
				beta = block;
			}
			else if (charlie == null) {
				charlie = block;
			}
			else if (delta == null) {
				delta = block;
			}
		}

		final Database DB = new Database();
		
		String sp1 = Base64.getEncoder().encodeToString(alpha);
		String sp2 = Base64.getEncoder().encodeToString(beta);
		String sp3 = Base64.getEncoder().encodeToString(charlie);
		String sp4 = null;
		if (delta != null) {
			sp4 = Base64.getEncoder().encodeToString(delta);
		}
		
		byte[] encKeyBytes = encKey.getBytes("UTF-8");
		byte[] keyBlock = FileSecure.getXORKeyBlock(b1, b2, b3, b4, encKeyBytes);
		String encodedKeyBlock = Base64.getEncoder().encodeToString(keyBlock);
		
		byte[] parBlock = FileSecure.getXORParBlock(alpha, beta, charlie, delta);
		String encodedParBlock = Base64.getEncoder().encodeToString(parBlock);
		
		DB.uploadFile(username, fileName, fileType, fileSize, dateCreated, sp1, sp2, sp3, sp4, encodedKeyBlock, encodedParBlock, arrByteArr.length);
	}

	public static void main(String[] args) throws Exception {
		
	}

}
