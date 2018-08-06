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
	
	public static void splitFile(File f, FileStorage fs, String username) throws Exception {
		int partCounter = 0;

		int sizeOfFiles = (int) (f.length() / 3);
		byte[] buffer = new byte[sizeOfFiles];

		String encKey = FileSecure.generateEncKey();
		
		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		byte[] alpha = null, beta = null, charlie = null, delta = null;
		
		try (FileInputStream fis = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;

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
		
		fs.setUsername(username);
		fs.setFileName(f.getName());
		fs.setFileType(FilenameUtils.getExtension(f.getName()).toUpperCase());
		fs.setFileSize(FileUtils.byteCountToDisplaySize(f.length()));
		fs.setDateCreated(FileStorage.convertTimestamp(Timestamp.from(Instant.now())));
		fs.setFile(Base64.getEncoder().encodeToString(Files.readAllBytes(f.toPath())));
		
		fs.setSplitFile1(Base64.getEncoder().encodeToString(alpha));
		fs.setSplitFile2(Base64.getEncoder().encodeToString(beta));
		fs.setSplitFile3(Base64.getEncoder().encodeToString(charlie));
		if (delta != null) {
			fs.setSplitFile4(Base64.getEncoder().encodeToString(delta));
		}
		else {
			fs.setSplitFile4("Placeholder");
		}
		
		byte[] encKeyBytes = encKey.getBytes("UTF-8");
		byte[] keyBlock = FileSecure.getXORKeyBlock(b1, b2, b3, b4, encKeyBytes);
		
		fs.setKeyBlock(Base64.getEncoder().encodeToString(keyBlock));
		
		byte[] parBlock = FileSecure.getXORParBlock(alpha, beta, charlie, delta);
		fs.setParBlock(Base64.getEncoder().encodeToString(parBlock));
		
		fs.setNoOfFiles(partCounter);
	}

	public static List<File> listOfFilesToMerge(String username, String fileName) throws IOException, Exception {
		FileStorage fs = FileStorage.getSplitFiles(username, fileName);
		byte[] splitFile1 = Base64.getDecoder().decode(fs.getSplitFile1());
		byte[] splitFile2 = Base64.getDecoder().decode(fs.getSplitFile2());
		byte[] splitFile3 = Base64.getDecoder().decode(fs.getSplitFile3());
		byte[] splitFile4 = Base64.getDecoder().decode(fs.getSplitFile4());
		byte[] keyBlock = Base64.getDecoder().decode(fs.getKeyBlock());
		byte[] parBlock = Base64.getDecoder().decode(fs.getParBlock());
		int noOfFiles = fs.getNoOfFiles();
		
		//Check if any split files missing, get missing file if there is:
		if (noOfFiles == 3) {
		  	if (splitFile1 == null || splitFile2 == null || splitFile3 == null) {
		  		if (splitFile1 == null) {
		  			splitFile1 = FileSecure.getMissingBlockByXOR(parBlock, splitFile2, splitFile3, splitFile4);
		  			fs.setSplitFile1(Base64.getEncoder().encodeToString(splitFile1));
		  			FileStorage.writeMissingFile(fs, 1);
		  		}
		  		else if (splitFile2 == null) {
		  			splitFile2 = FileSecure.getMissingBlockByXOR(splitFile1, parBlock, splitFile3, splitFile4);
		  			fs.setSplitFile2(Base64.getEncoder().encodeToString(splitFile2));
		  			FileStorage.writeMissingFile(fs, 2);
		  		}
		  		else if (splitFile3 == null) {
		  			splitFile3 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, parBlock, splitFile4);
		  			fs.setSplitFile3(Base64.getEncoder().encodeToString(splitFile3));
		  			FileStorage.writeMissingFile(fs, 3);
		  		}
		  	}
		 }
		 else if (noOfFiles == 4) {
		  	if (splitFile1 == null || splitFile2 == null || splitFile3 == null || splitFile4 == null) {
		  		if (splitFile1 == null) {
		  			splitFile1 = FileSecure.getMissingBlockByXOR(parBlock, splitFile2, splitFile3, splitFile4);
		  			fs.setSplitFile1(Base64.getEncoder().encodeToString(splitFile1));
		  			FileStorage.writeMissingFile(fs, 1);
		  		}
		  		else if (splitFile2 == null) {
		  			splitFile2 = FileSecure.getMissingBlockByXOR(splitFile1, parBlock, splitFile3, splitFile4);
		  			fs.setSplitFile2(Base64.getEncoder().encodeToString(splitFile2));
		  			FileStorage.writeMissingFile(fs, 2);
		  		}
		  		else if (splitFile3 == null) {
		  			splitFile3 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, parBlock, splitFile4);
		  			fs.setSplitFile3(Base64.getEncoder().encodeToString(splitFile3));
		  			FileStorage.writeMissingFile(fs, 3);
		  		}
		  		else if (splitFile4 == null) {
		  			splitFile4 = FileSecure.getMissingBlockByXOR(splitFile1, splitFile2, splitFile3, parBlock);
		  			fs.setSplitFile4(Base64.getEncoder().encodeToString(splitFile4));
		  			FileStorage.writeMissingFile(fs, 4);
		  		}
		  	}
		 }
		 
		 //Calculate hash values of split files from database:
		 byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		 b1 = FileSecure.hash(splitFile1);
		 b2 = FileSecure.hash(splitFile2);
		 b3 = FileSecure.hash(splitFile3);
		 if (noOfFiles != 3) {
			 b4 = FileSecure.hash(splitFile4);
		 }
		 
		 //Get encryption key from keyBlock:
		 byte[] encKeyBytes = FileSecure.getEncKeyByXOR(b1, b2, b3, b4, keyBlock);
		 String encKey = new String(encKeyBytes, "UTF-8");
		 
		 //Decrypt the split files:
		 byte[] decryptedFile1 = FileSecure.decrypt(splitFile1, encKey);
		 byte[] decryptedFile2 = FileSecure.decrypt(splitFile2, encKey);
		 byte[] decryptedFile3 = FileSecure.decrypt(splitFile3, encKey);
		 byte[] decryptedFile4 = null;
		 if (noOfFiles != 3) {
			 decryptedFile4 = FileSecure.decrypt(splitFile4, encKey);
		 }
		 
		 //Write to temporary files:
		 File file1 = File.createTempFile("File1", ".tmp");
		 FileUtils.writeByteArrayToFile(file1, decryptedFile1);
		 File file2 = File.createTempFile("File2", ".tmp");
		 FileUtils.writeByteArrayToFile(file2, decryptedFile2);
		 File file3 = File.createTempFile("File3", ".tmp");
		 FileUtils.writeByteArrayToFile(file3, decryptedFile3);
		 File file4 = File.createTempFile("File4", ".tmp");
		 if (noOfFiles != 3) {
			 FileUtils.writeByteArrayToFile(file4, decryptedFile4);
		 }
		 
		 //Store in a list of files:
		 List<File> listOfFiles = new ArrayList<File>();
		 listOfFiles.add(file1);
		 listOfFiles.add(file2);
		 listOfFiles.add(file3);
		 if (noOfFiles != 3) {
			 listOfFiles.add(file4);
		 }
		 
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
