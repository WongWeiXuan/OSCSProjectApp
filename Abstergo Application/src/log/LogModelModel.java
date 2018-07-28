package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.ehcache.Cache;

import logExtra.AESEncryption;
import logExtra.BlockChain;
import logExtra.Transcation;
import login.controller.LoginPageController;

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
			Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
			byte [] keyByte = Base64.getDecoder().decode(LogDAO.getEncryptionKey(userCache.get("User")));
			AESEncryption aes = new AESEncryption(keyByte);
			File decryptedFile = aes.decryptFile(file);
			return processFile(decryptedFile);
		}
		
		return null;
	}
	
	public ArrayList<LogDetailsModel> getLogsDetailsModel(int select) {
		File file = fileSelection(select);
		if(file != null) {
			// Get user private key to decrypt
			Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
			byte [] keyByte = Base64.getDecoder().decode(LogDAO.getEncryptionKey(userCache.get("User")));
			AESEncryption aes = new AESEncryption(keyByte);
			File decryptedFile = aes.decryptFile(file);
			return processFileModel(decryptedFile);
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
	
	private ArrayList<LogDetailsModel> processFileModel(File decrypted) {
		LineIterator li = null;
		try {
			li = FileUtils.lineIterator(decrypted, "UTF-8");
			ArrayList<LogDetailsModel> array = new ArrayList<LogDetailsModel>();
			
			while(li.hasNext()) {
				String line = li.nextLine();
				Scanner sc = new Scanner(line);
				sc.useDelimiter(";");
				array.add(new LogDetailsModel(sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next(), sc.next()));
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
				name = "System";
				break;
			case 2:
				name = "Security";
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
	
	public static File getFile(String logFileName) {
		return new File("src/resource/logs/" + logFileName + "Log.txt");
	}
	
	public static boolean updateOrGetLogs(String logName) {
		// Retrieve user
		Cache<String, String> userCache = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class);
		String username = userCache.get("User");
		
		if(getFile(logName) == null) {
			// Retrieve key
			String key = LogDAO.getEncryptionKey(username);
			LogModel log = new LogModel(logName);
			log.openEventLog();
			File file = log.read(key);
			AESEncryption aes = new AESEncryption(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)));
			File encrypted = aes.encryptFile(file);
			file.delete();
			LogDAO.writeToFile(encrypted, "src/resource/logs/" + logName + "Log.txt");
		}
		
		// Check Integrity
		File temp = getFile(logName);
		String hash = Transcation.generateSHA(temp); // Get fileHash
		BlockChain chain = new BlockChain(logName, "", false);
		String chainHash = null;
		
		if((chainHash = chain.getLastFileHash()) == null || hash.equals(chainHash)) { // IF hash matches OR chain is empty
			System.out.println("\nHASH EQUALS / NULL: " + hash + " = " + chainHash);
			// Retrieve key
			String key = LogDAO.getEncryptionKey(username);
			LogModel log = new LogModel(logName);
			log.openEventLog();
			File file = log.read(key);
			AESEncryption aes = new AESEncryption(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)));
			File encrypted = aes.encryptFile(file);
			file.delete();
			LogDAO.writeToFile(encrypted, "src/resource/logs/" + logName + "Log.txt");
			
			return true;
		} else {
			System.out.println("\nHASH DONT EQUALS: " + hash + " ; " + chainHash);
			return false;
		}
	}

	public static Map<String, Integer> processAndGetSummary24Hours(ArrayList<LogDetailsModel> logsDetailsModel, int select) {
		// To day last 24 hour
		long day = 86400000;
		long time = (System.currentTimeMillis() - day) / 1000;
		
		Map<String, Integer> mapping = new HashMap<String, Integer>();
		// Init
		int process = 0;
		int logged = 0;
		int share = 0;
		int connection = 0;
		int service = 0;
		int file = 0;
		
		for(LogDetailsModel m : logsDetailsModel) {
			long logTime = Long.parseLong(m.getEventTimeGenerated().substring(0, m.getEventTimeGenerated().length() - 1));
			if(logTime < time) {
				continue;
			}
			
			String eventID = m.getEventID();
			if("4688.".equals(eventID) || "592.".equals(eventID)) {
				process++;
			} else if("4624.".equals(eventID) || "528.".equals(eventID) || "540.".equals(eventID)) {
				logged++;
			} else if("5140.".equals(eventID) || "560.".equals(eventID)) {
				share++;
			} else if("5156.".equals(eventID)) {
				connection++;
			} else if("7045.".equals(eventID) || "601.".equals(eventID)) {
				service++;
			} else if("4663.".equals(eventID) || "567.".equals(eventID)) {
				file++;
			} 
		}
		
		mapping.put("process", process);
		mapping.put("logged", logged);
		mapping.put("share", share);
		mapping.put("connection", connection);
		mapping.put("service", service);
		mapping.put("file", file);
		
		return mapping;
	}
	
	public static Map<String, Integer> processAndGetSummary1Week(ArrayList<LogDetailsModel> logsDetailsModel, int select) {
		// To day last 1 Week
		long week = 604800000;
		long time = (System.currentTimeMillis() - week) / 1000;
		
		Map<String, Integer> mapping = new HashMap<String, Integer>();
		// Init
		int process = 0;
		int logged = 0;
		int share = 0;
		int connection = 0;
		int service = 0;
		int file = 0;
		
		for(LogDetailsModel m : logsDetailsModel) {
			long logTime = Long.parseLong(m.getEventTimeGenerated().substring(0, m.getEventTimeGenerated().length() - 1));
			if(logTime < time) {
				continue;
			}
			
			String eventID = m.getEventID();
			if("4688.".equals(eventID) || "592.".equals(eventID)) {
				process++;
			} else if("4624.".equals(eventID) || "528.".equals(eventID) || "540.".equals(eventID)) {
				logged++;
			} else if("5140.".equals(eventID) || "560.".equals(eventID)) {
				share++;
			} else if("5156.".equals(eventID)) {
				connection++;
			} else if("7045.".equals(eventID) || "601.".equals(eventID)) {
				service++;
			} else if("4663.".equals(eventID) || "567.".equals(eventID)) {
				file++;
			} 
		}
		
		mapping.put("process", process);
		mapping.put("logged", logged);
		mapping.put("share", share);
		mapping.put("connection", connection);
		mapping.put("service", service);
		mapping.put("file", file);
		
		return mapping;
	}
}
