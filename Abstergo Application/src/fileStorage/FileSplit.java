package fileStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class FileSplit {
	
	public static void splitFile(File f, FileStorage fs) throws Exception {
		int partCounter = 0;

		int sizeOfFiles = (int) (f.length() / 3);
		byte[] buffer = new byte[sizeOfFiles];

		String encKey = FileSecure.generateEncKey();
		
		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		byte[] alpha = null, beta = null, charlie = null, delta = null;
		
		try (FileInputStream fis = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			System.out.println("Encryption key: " + encKey);

			while ((bytesAmount = bis.read(buffer)) != -1) {
				byte[] block = FileSecure.encrypt(Arrays.copyOfRange(buffer, 0, bytesAmount), encKey);
				partCounter++;
				
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
		}
		
		fs.setFile(Files.readAllBytes(f.toPath()));
		fs.setFileName(f.getName());
		fs.setFileType(FilenameUtils.getExtension(f.getName()).toUpperCase());
		fs.setDateCreated(FileStorage.convertTimestamp(Timestamp.from(Instant.now())));
		
		fs.setSplitFile1(alpha);
		fs.setSplitFile2(beta);
		fs.setSplitFile3(charlie);
		fs.setSplitFile4(delta);
		
		fs.setNoOfFiles(partCounter);
		
		byte[] encKeyBytes = encKey.getBytes("UTF-8");
		byte[] keyBlock = FileSecure.getXORKeyBlock(b1, b2, b3, b4, encKeyBytes);
		
		fs.setKeyBlock(keyBlock);
		
		//XOR to get parBlock
		byte[] parBlock = FileSecure.getXORParBlock(alpha, beta, charlie, delta);
		fs.setParBlock(parBlock);
	}

	public static List<File> listOfFilesToMerge(String username, String fileName) throws IOException, Exception {
		FileStorage fs = FileStorage.getSplitFiles(username, fileName);
		byte[] splitFile1 = fs.getSplitFile1();
		byte[] splitFile2 = fs.getSplitFile2();
		byte[] splitFile3 = fs.getSplitFile3();
		byte[] splitFile4 = fs.getSplitFile4();
		byte[] keyBlock = fs.getKeyBlock();
		byte[] parBlock = fs.getParBlock();
		int noOfFiles = fs.getNoOfFiles();
		
		//Check if any split files missing, get missing file if there is:
		if (noOfFiles == 3) {
		  	if (splitFile1 == null || splitFile2 == null || splitFile3 == null) {
		  		if (splitFile1 == null) {
		  			splitFile1 = FileSecure.getMissingBlockByXOR(parBlock, splitFile2, splitFile3, splitFile4);
		  		}
		  		else if (splitFile2 == null) {
		  			splitFile2 = FileSecure.getMissingBlockByXOR(splitFile1, parBlock, splitFile3, splitFile4);
		  		}
		  		else if (splitFile3 == null) {
		  			splitFile3 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, parBlock, splitFile4);
		  		}
		  	}
		 }
		 else if (noOfFiles == 4) {
		  	if (splitFile1 == null || splitFile2 == null || splitFile3 == null || splitFile4 == null) {
		  		if (splitFile1 == null) {
		  			splitFile1 = FileSecure.getMissingBlockByXOR(parBlock, splitFile2, splitFile3, splitFile4);
		  		}
		  		else if (splitFile2 == null) {
		  			splitFile2 = FileSecure.getMissingBlockByXOR(splitFile1, parBlock, splitFile3, splitFile4);
		  		}
		  		else if (splitFile3 == null) {
		  			splitFile3 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, parBlock, splitFile4);
		  		}
		  		else if (splitFile4 == null) {
		  			splitFile4 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, splitFile3, parBlock);
		  		}
		  	}
		 }
		  
		 //Calculate hash values of split files from database:
		 byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		 b1 = FileSecure.hash(splitFile1);
		 b2 = FileSecure.hash(splitFile2);
		 b3 = FileSecure.hash(splitFile3);
		 b4 = FileSecure.hash(splitFile4);
		  
		 //Get encryption key from keyBlock:
		 byte[] encKeyBytes = FileSecure.getEncKeyByXOR(b1, b2, b3, b4, keyBlock);
		 String encKey = new String(encKeyBytes, "UTF-8");
		  
		 //Decrypt the split files:
		 byte[] decryptedFile1 = FileSecure.decrypt(splitFile1, encKey);
		 byte[] decryptedFile2 = FileSecure.decrypt(splitFile2, encKey);
		 byte[] decryptedFile3 = FileSecure.decrypt(splitFile3, encKey);
		 byte[] decryptedFile4 = FileSecure.decrypt(splitFile4, encKey);
		  
		 //Write to temporary files:
		 File file1 = File.createTempFile("File1", ".tmp");
		 FileUtils.writeByteArrayToFile(file1, decryptedFile1);
		 File file2 = File.createTempFile("File2", ".tmp");
		 FileUtils.writeByteArrayToFile(file2, decryptedFile2);
		 File file3 = File.createTempFile("File3", ".tmp");
		 FileUtils.writeByteArrayToFile(file3, decryptedFile3);
		 File file4 = File.createTempFile("File4", ".tmp");
		 FileUtils.writeByteArrayToFile(file4, decryptedFile4);
		  
		 //Store in a list of files:
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
