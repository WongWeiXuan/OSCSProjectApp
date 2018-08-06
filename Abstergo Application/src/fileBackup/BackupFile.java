package fileBackup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import fileStorage.FileSecure;
import fileStorage.FileStorage;

public class BackupFile {
	
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

	public static FileBackup mergeFiles(List<File> files, File into, String username, String encKey) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(into);
				BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
			
			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);
				f.delete();
			}
		}
		
		ArrayList<FileBackup> fbList = FileBackup.getBackupFileNames(username);
		int count = 0;
		String finFileName = null;
		
		if (fbList.size() == 0) {
			finFileName = into.getName();
		}
		else {
			for (FileBackup fb : fbList) {
				if (into.getName().equals(fb.getFileName())) {
					count++;
					finFileName = FilenameUtils.removeExtension(into.getName()) + "(" + count + ")." + FilenameUtils.getExtension(into.getName());
				}
				else {
					finFileName = into.getName();
				}
			}
		}
		
		FileBackup fb = new FileBackup();
		fb.setUsername(username);
		fb.setFileName(finFileName);
		fb.setFileType(FilenameUtils.getExtension(into.getName()).toUpperCase());
		fb.setFileSize(FileUtils.byteCountToDisplaySize(into.length()));
		fb.setFileData(Base64.getEncoder().encodeToString(FileSecure.encrypt(Files.readAllBytes(into.toPath()), encKey)));
		fb.setEncKey(encKey);
		fb.setDateCreated(FileBackup.convertTimestamp(Timestamp.from(Instant.now())));
		fb.setFileBackupIndex(into.getName());
		
		into.delete();
		
		return fb;
	}
	
	public static byte[] recoverFile(String encryptedBackupFile, String encKey) throws Exception {
		byte[] byteToDecrypt = Base64.getDecoder().decode(encryptedBackupFile);
		byte[] decryptedBackupFile = FileSecure.decrypt(byteToDecrypt, encKey);
		return decryptedBackupFile;
	}

	public static void main(String[] args) {
		
	}

}
