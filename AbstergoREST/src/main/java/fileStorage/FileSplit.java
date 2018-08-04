package fileStorage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import AbstergoREST.main.database.Database;
import AbstergoREST.main.model.FileStorage;

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

	public static void splitFile(String username, String fileName, String fileType, String dateCreated, byte[] file) throws Exception {
		byte[][] arrByteArr = splitBytes(file, file.length / 3);
		
		String encKey = FileSecure.generateEncKey();

		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		byte[] alpha = null, beta = null, charlie = null, delta = null;


		System.out.println("Encryption key: " + encKey);
		
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

		byte[] encKeyBytes = encKey.getBytes("UTF-8");
		byte[] keyBlock = FileSecure.getXORKeyBlock(b1, b2, b3, b4, encKeyBytes);

		byte[] parBlock = FileSecure.getXORParBlock(alpha, beta, charlie, delta);

		DB.uploadFile(username, fileName, fileType, dateCreated, alpha, beta, charlie, delta, keyBlock, parBlock, arrByteArr.length);
	}

	public static List<File> listOfFilesToMerge(String username, String fileName) throws IOException, Exception {
		final Database DB = new Database();

		FileStorage fs = DB.downloadFile(username, fileName);
		byte[] splitFile1 = fs.getSplitFile1();
		byte[] splitFile2 = fs.getSplitFile2();
		byte[] splitFile3 = fs.getSplitFile3();
		byte[] splitFile4 = fs.getSplitFile4();
		byte[] keyBlock = fs.getKeyBlock();
		byte[] parBlock = fs.getParBlock();
		int noOfFiles = fs.getNoOfFiles();

		if (noOfFiles == 3) {
			if (splitFile1 == null || splitFile2 == null || splitFile3 == null) {
				if (splitFile1 == null) {
					splitFile1 = FileSecure.getMissingBlockByXOR(parBlock, splitFile2, splitFile3, splitFile4);
				} else if (splitFile2 == null) {
					splitFile2 = FileSecure.getMissingBlockByXOR(splitFile1, parBlock, splitFile3, splitFile4);
				} else if (splitFile3 == null) {
					splitFile3 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, parBlock, splitFile4);
				}
			}
		} else if (noOfFiles == 4) {
			if (splitFile1 == null || splitFile2 == null || splitFile3 == null || splitFile4 == null) {
				if (splitFile1 == null) {
					splitFile1 = FileSecure.getMissingBlockByXOR(parBlock, splitFile2, splitFile3, splitFile4);
				} else if (splitFile2 == null) {
					splitFile2 = FileSecure.getMissingBlockByXOR(splitFile1, parBlock, splitFile3, splitFile4);
				} else if (splitFile3 == null) {
					splitFile3 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, parBlock, splitFile4);
				} else if (splitFile4 == null) {
					splitFile4 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, splitFile3, parBlock);
				}
			}
		}

		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		b1 = FileSecure.hash(splitFile1);
		b2 = FileSecure.hash(splitFile2);
		b3 = FileSecure.hash(splitFile3);
		b4 = FileSecure.hash(splitFile4);

		byte[] encKeyBytes = FileSecure.getEncKeyByXOR(b1, b2, b3, b4, keyBlock);
		String encKey = new String(encKeyBytes, "UTF-8");

		byte[] decryptedFile1 = FileSecure.decrypt(splitFile1, encKey);
		byte[] decryptedFile2 = FileSecure.decrypt(splitFile2, encKey);
		byte[] decryptedFile3 = FileSecure.decrypt(splitFile3, encKey);
		byte[] decryptedFile4 = FileSecure.decrypt(splitFile4, encKey);

		File file1 = File.createTempFile("File1", ".tmp");
		FileUtils.writeByteArrayToFile(file1, decryptedFile1);
		File file2 = File.createTempFile("File2", ".tmp");
		FileUtils.writeByteArrayToFile(file2, decryptedFile2);
		File file3 = File.createTempFile("File3", ".tmp");
		FileUtils.writeByteArrayToFile(file3, decryptedFile3);
		File file4 = File.createTempFile("File4", ".tmp");
		FileUtils.writeByteArrayToFile(file4, decryptedFile4);

		List<File> listOfFiles = new ArrayList<File>();
		listOfFiles.add(file1);
		listOfFiles.add(file2);
		listOfFiles.add(file3);
		listOfFiles.add(file4);

		return listOfFiles;
	}

	public static File mergeFiles(List<File> files, File into) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(into);
				BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {

			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);
				f.delete();
			}
		}

		return into;
	}

	public static void main(String[] args) throws Exception {

	}

}
