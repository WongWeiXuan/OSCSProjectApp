package fileStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FileSplit {
	
	public void splitFile(File f) throws Exception {
		int partCounter = 1;

		int sizeOfFiles = (int) (f.length() / 3);
		byte[] buffer = new byte[sizeOfFiles];

		String fileName = f.getName();
		String encKey = FileSecure.generateEncKey();
		
		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		byte[] alpha = null, beta = null, charlie = null, delta = null;
		
		try (FileInputStream fis = new FileInputStream(f);
				BufferedInputStream bis = new BufferedInputStream(fis)) {

			int bytesAmount = 0;
			System.out.println("Encryption key: " + encKey);

			while ((bytesAmount = bis.read(buffer)) != -1) {
				String filePartName = String.format("%s.%03d", fileName, partCounter++);
				File newFile = new File("D:\\FileTest\\", filePartName);
				
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					byte[] block = FileSecure.encrypt(Arrays.copyOfRange(buffer, 0, bytesAmount), encKey);
					out.write(block);
					
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
		}
		
		//XOR hash blocks with encryption key
		byte[] encKeyBytes = encKey.getBytes("UTF-8");
		byte[] keyBlock = FileSecure.getXORKeyBlock(b1, b2, b3, b4, encKeyBytes);
		
		//Write key block to file
		String keyBlockFileName = String.format("%s.%03d.%s", fileName, partCounter++, "keyblock");
		File keyBlockFile = new File("D:\\FileTest\\", keyBlockFileName);
		FileUtils.writeByteArrayToFile(keyBlockFile, keyBlock);
		
		//XOR to get parity block
		byte[] parBlock = FileSecure.getXORParBlock(alpha, beta, charlie, delta);
		
		//Write parity block to file
		String parBlockFileName = String.format("%s.%03d.%s", fileName, partCounter++, "parblock");
		File parBlockFile = new File("D:\\FileTest\\", parBlockFileName);
		FileUtils.writeByteArrayToFile(parBlockFile, parBlock);
	}

	public List<File> listOfFilesToMerge(File oneOfFiles) throws IOException, Exception {
		String tmpName = oneOfFiles.getName();
		String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));
		File[] files = oneOfFiles.getParentFile().listFiles((File dir, String name) -> name.matches(destFileName + "[.]\\d+"));
		
		File keyBlockFile = oneOfFiles.getParentFile().listFiles((File dir, String name) -> name.startsWith(destFileName) && name.endsWith(".keyblock"))[0];
		byte[] keyBlock = Files.readAllBytes(keyBlockFile.toPath());
		String keyStr = keyBlockFile.getName().substring(keyBlockFile.getName().indexOf(destFileName), keyBlockFile.getName().lastIndexOf('.'));
		String keyNum = keyStr.substring(keyStr.lastIndexOf('.') + 1);
		
		byte[] parBlock = null;
		String parNum = null;
		int missingPartNum = 0;
		
		//If no split files are missing, no need for parity block
		if (Integer.parseInt(keyNum) - files.length == 2) {
			File parBlockFile = oneOfFiles.getParentFile().listFiles((File dir, String name) -> name.startsWith(destFileName) && name.endsWith(".parblock"))[0];
			parBlock = Files.readAllBytes(parBlockFile.toPath());
			String parStr = parBlockFile.getName().substring(parBlockFile.getName().indexOf(destFileName), parBlockFile.getName().lastIndexOf('.'));
			parNum = parStr.substring(parStr.lastIndexOf('.') + 1);
			
			//Find the missing file, then create an empty file
			int partCounter = 1;
			for (File f : files) {
				int partNum = Integer.parseInt(f.getName().substring(f.getName().lastIndexOf('.') + 1));
				partCounter++;
				if (partNum != partCounter) {
					missingPartNum = partCounter;
					System.out.println("missingPartNum = partCounter = " + partCounter);
				}
			}
			if (missingPartNum == 0) {
				missingPartNum = 1;
			}
			String missingFileName = String.format("%s.%03d", destFileName, missingPartNum);
			File missingFile = new File("D:\\FileTest\\", missingFileName);
			files = Arrays.copyOf(files, files.length + 1);
			files[files.length - 1] = missingFile;
			missingFile.createNewFile();
		}
		
		Arrays.sort(files);
		
		byte[] alpha = null, beta = null, charlie = null, delta = null;
		byte[] b1 = null, b2 = null, b3 = null, b4 = null;
		
		for (File f : files) {
			if (b1 == null) {
				alpha = Files.readAllBytes(f.toPath());
				b1 = FileSecure.hash(alpha);
				//System.out.println(Base64.getEncoder().encodeToString(alpha));
			}
			else if (b2 == null) {
				beta = Files.readAllBytes(f.toPath());
				b2 = FileSecure.hash(beta);
				//System.out.println(Base64.getEncoder().encodeToString(beta));
			}
			else if (b3 == null) {
				charlie = Files.readAllBytes(f.toPath());
				b3 = FileSecure.hash(charlie);
				//System.out.println(Base64.getEncoder().encodeToString(charlie));
			}
			else if (b4 == null) {
				delta = Files.readAllBytes(f.toPath());
				b4 = FileSecure.hash(delta);
			}
		}
		//System.out.println(delta);
		if (parBlock != null) {
			if (missingPartNum == 1) {
				alpha = FileSecure.getMissingBlockByXOR(parBlock, beta, charlie, delta);
				File f = new File("D:\\FileTest\\", destFileName + ".001");
				FileUtils.writeByteArrayToFile(f, alpha);
				b1 = FileSecure.hash(alpha);
			}
			else if (missingPartNum == 2) {
				beta = FileSecure.getMissingBlockByXOR(alpha, parBlock, charlie, delta);
				File f = new File("D:\\FileTest\\", destFileName + ".002");
				FileUtils.writeByteArrayToFile(f, beta);
				b2 = FileSecure.hash(beta);
			}
			else if (missingPartNum == 3) {
				charlie = FileSecure.getMissingBlockByXOR(alpha, beta, parBlock, delta);
				File f = new File("D:\\FileTest\\", destFileName + ".003");
				FileUtils.writeByteArrayToFile(f, charlie);
				b3 = FileSecure.hash(charlie);
			}
			else if (missingPartNum == 4) {
				delta = FileSecure.getMissingBlockByXOR(alpha, beta, charlie, parBlock);
				File f = new File("D:\\FileTest\\", destFileName + ".004");
				FileUtils.writeByteArrayToFile(f, delta);
				b4 = FileSecure.hash(delta);
			}
		}
		
		File file;
		List<File> fileList = new ArrayList<File>();
		byte[] encKeyBytes = FileSecure.getEncKeyByXOR(b1, b2, b3, b4, keyBlock);
		String encKey = new String(encKeyBytes, "UTF-8");
		
		//System.out.println("Decryption key: " + encKey);
		
		for (File f : files) {
			file = f;
			FileUtils.writeByteArrayToFile(file, FileSecure.decrypt(Files.readAllBytes(file.toPath()), encKey));
			fileList.add(file);
		}
		
		return fileList;
	}

	public void mergeFiles(List<File> files, File into) throws Exception {
		try (FileOutputStream fos = new FileOutputStream(into);
				BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
			
			String fileName = files.get(0).getName();
			String destFileName = fileName.substring(0, fileName.lastIndexOf('.'));
			String encKey = FileSecure.generateEncKey();
			
			byte[] b1 = null, b2 = null, b3 = null, b4 = null;
			byte[] alpha = null, beta = null, charlie = null, delta = null;
			
			for (File f : files) {
				Files.copy(f.toPath(), mergingStream);
				byte[] block = FileSecure.encrypt(Files.readAllBytes(f.toPath()), encKey);
				FileUtils.writeByteArrayToFile(f, block);
				
				if (b1 == null) {
					b1 = FileSecure.hash(Files.readAllBytes(f.toPath()));
				}
				else if (b2 == null) {
					b2 = FileSecure.hash(Files.readAllBytes(f.toPath()));
				}
				else if (b3 == null) {
					b3 = FileSecure.hash(Files.readAllBytes(f.toPath()));
				}
				else if (b4 == null) {
					b4 = FileSecure.hash(Files.readAllBytes(f.toPath()));
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
			
			//XOR hash blocks with encryption key
			byte[] encKeyBytes = encKey.getBytes("UTF-8");
			byte[] keyBlock = FileSecure.getXORKeyBlock(b1, b2, b3, b4, encKeyBytes);
			
			//Write key block to file
			String keyBlockFileName = String.format("%s.%03d.%s", destFileName, files.size() + 1, "keyblock");
			File keyBlockFile = new File("D:\\FileTest\\", keyBlockFileName);
			FileUtils.writeByteArrayToFile(keyBlockFile, keyBlock);
			
			//XOR to get parity block
			byte[] parBlock = FileSecure.getXORParBlock(alpha, beta, charlie, delta);
			
			//Write parity block to file
			String parBlockFileName = String.format("%s.%03d.%s", destFileName, files.size() + 2, "parblock");
			File parBlockFile = new File("D:\\FileTest\\", parBlockFileName);
			FileUtils.writeByteArrayToFile(parBlockFile, parBlock);
		}
	}
	
	public static void main(String[] args) throws Exception {
		FileSplit fs = new FileSplit();
		
		//fs.splitFile(new File("D:\\Software Application Manual\\Notebook_SWmanual_v3.1.pdf"));
		//fs.mergeFiles(fs.listOfFilesToMerge(new File("D:\\FileTest\\Notebook_SWmanual_v3.1.pdf.001")), new File("D:\\FileTest\\Notebook_SWmanual_v3.1.pdf"));

		fs.splitFile(new File("C:\\Users\\serwe\\Pictures\\Saved Pictures\\Sora.png"));
		fs.mergeFiles(fs.listOfFilesToMerge(new File("D:\\FileTest\\Sora.png.001")), new File("D:\\FileTest\\Sora.png"));
		System.out.println("Is merged picture same as original: " + FileUtils.contentEquals(new File("C:\\Users\\serwe\\Pictures\\Saved Pictures\\Sora.png"), new File("D:\\FileTest\\Sora.png")));
		
		//fs.splitFile(new File("D:\\Hello World.txt"));
		//fs.mergeFiles(fs.listOfFilesToMerge(new File("D:\\FileTest\\Hello World.txt.001")), new File("D:\\FileTest\\Hello World.txt"));
		//System.out.println("Is merged file same as original: " + FileUtils.contentEquals(new File("D:\\Hello World.txt"), new File("D:\\FileTest\\Hello World.txt")));
		
		//fs.splitFile(new File("C:\\Users\\serwe\\Desktop\\HombaseReturn_OutpostIntro.mp4"));
		//fs.mergeFiles(fs.listOfFilesToMerge(new File("D:\\FileTest\\HombaseReturn_OutpostIntro.mp4.001")), new File("D:\\FileTest\\HombaseReturn_OutpostIntro.mp4"));
	}

}
