package log;

import java.io.File;
import java.util.Base64;
import java.util.Scanner;

import log.controller.LogPageController;
import logExtra.Client;
import logExtra.HandshakeThread;
import logExtra.KeyPairGen;
import logExtra.Lock;
import login.controller.LoginPageController;
import setting.Setting;

public class LogModelThread implements Runnable{
	private static Client client;
	private static String logName;
	private volatile static boolean running;
	private volatile boolean sleeping;
	private String username;
	public static Lock lock = new Lock();
	
	public LogModelThread(String logName1) {
		client = new Client(logName1);
		logName = logName1;
		running = true;
		sleeping = false;
		this.username = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class).get("User");
	}
	
	@Override
	public void run() {
		while(running) {
			while(!sleeping) {
				Setting setting = new Setting();
				for(String s : setting.getPreference().getLogFile()) {
					System.out.println("SETTING: " + s);
					try {
						lock.lock();
						logName = s;
						HandshakeThread.setLogName(s);
						// Update event log
						// Encrypt
						if(!LogModelModel.updateOrGetLogs(logName)) {
							// Popup to display
							LogPageController.setPopupText(logName + " logfile failed integrity check, downloading one now...");
							client.requestFileTransferRequest(logName);

							// Sign (private key) and find its signed file's hash // Check for private public key
							// Create Transcation new Transcation("UserFROM", "UserTO", EncryptedFile, signedFilesHash);
							// Get previous block hash
							// Create Block new Block(Transcation, previousBlock/Hash);
							// Mine (2 mins)
							
							LogPageController.setPopupText("Sending file now...");
							// Send
							File file = LogModelModel.getFile(logName);
							String signature = Base64.getEncoder().encodeToString(KeyPairGen.signFile(file, username));
							client.sendFileTransferRequest(logName, username, signature);
							
							LogPageController.setPopupTextDefault();
						} else {

							// Sign (private key) and find its signed file's hash // Check for private public key
							// Create Transcation new Transcation("UserFROM", "UserTO", EncryptedFile, signedFilesHash);
							// Get previous block hash
							// Create Block new Block(Transcation, previousBlock/Hash);
							// Mine (2 mins)
							LogPageController.setPopupText("Sending file now...");
							// Send
							File file = LogModelModel.getFile(logName);
							String signature = Base64.getEncoder().encodeToString(KeyPairGen.signFile(file, username));
							client.sendFileTransferRequest(logName, username, signature);
							LogPageController.setPopupTextDefault();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// Set sleep = true;
				sleeping = true;
			}
			// Sleep for 3 mins
			try {
				System.out.println("Sleeping");
				Thread.sleep(180000);
				sleeping = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Method to stop the thread
	public synchronized static void stop() {
		running = false;
		client.close();
	}
	
	public void requestFileTransferRequest() {
		client.getListOfAvailableNetwork();
		client.requestFileTransferRequest(logName);
	}
	
	public void updateLogs() {
		if(!LogModelModel.updateOrGetLogs(logName)) {
			// Popup to display
			LogPageController.setPopupText(logName + " logfile failed integrity check, downloading one now...");
			client.requestFileTransferRequest(logName);
			
			LogPageController.setPopupTextDefault();
		}
	}
	
	public static boolean isRunning() {
		return running;
	}
	
	public static void setLogName(String logName1) {
		logName = logName1;
	}
	
	public static void main(String[] arg0) {
		Client client = new Client("Application");
		
		boolean on = true;
		
		System.out.println("Listener: ON");
		Scanner sc = new Scanner(System.in);
		while(on) {
			System.out.println("Select your action: ");
			System.out.println("\t1. Get all available networks.");
			System.out.println("\t2. Send File Transfer Reqeust.");
			System.out.println("\t3. Send Request File Transfer Reqeust.");
			System.out.println("\t4. EXIT.");
			
			int select = sc.nextInt();
			System.out.println("");
			switch(select) {
				case 1:
					client.getListOfAvailableNetwork();
					break;
				case 2:
					String signature = Base64.getEncoder().encodeToString(KeyPairGen.signFile(LogModelModel.getFile("Application"), "Wolf"));
					client.sendFileTransferRequest("Application", "Wolf", signature);
					break;
				case 3:
					client.requestFileTransferRequest("Application");
					break;
				case 4:
					on = false;
					break;
				default:
					on = false;
			}
		}
		sc.close();
		client.close();
	}
}