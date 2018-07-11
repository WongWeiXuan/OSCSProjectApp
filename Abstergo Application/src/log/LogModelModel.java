package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import logExtra.AESEncryption;

// My previous LogModel is too messy, didn't feel like changing it so make this to simplify it
public class LogModelModel {
	private ArrayList<File> logFiles;
	
	public LogModelModel() {
		// Get where the logs are stored
		logFiles = LogDAO.getAllFile();
	}
	
	public ArrayList<LogDetailsTree> getLogsDetails(int select) {
		File file = fileSelection(select);
		if(file != null) {
			// Get user private key to decrypt
			// TODO
			String key = "l2nMvmLRUqY7JeQZgD9nHQ=="; // REMOVE THIS
			byte [] keyByte = Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
			AESEncryption aes = new AESEncryption(keyByte);
			File decryptedFile = aes.decryptFile(file);
			return processFile(decryptedFile);
		}
		
		return null;
	}
	
	public ArrayList<String> filterLogs(int select, String [] array) {
		return null;
	}
	
	private ArrayList<LogDetailsTree> processFile(File decrypted) {
		LineIterator li = null;
		try {
			li = FileUtils.lineIterator(decrypted, "UTF-8");
			ArrayList<LogDetailsTree> array = new ArrayList<LogDetailsTree>();
			
			while(li.hasNext()) {
				String line = li.nextLine();
				Scanner sc = new Scanner(line);
				sc.useDelimiter(";");
				array.add(new LogDetailsTree(sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next()));
				sc.close();
			}
			
			return array;
		} catch (FileNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING, "", (Object) e);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (li != null) {
					li.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private File fileSelection(int select) {
		String name;
		switch(select) {
			case 0:
				name = "Application";
				break;
			case 1:
				name = "Security";
				break;
			case 2:
				name = "System";
				break;
			case 3:
				name = "Abstergo";
				break;
			default:
				return null;
		}
		
		name += "Log.txt";
		for(File file : logFiles) {
			if(file.getName().contains(name)) {
				return file;
			}
		}
		
		return null;
	}
}
