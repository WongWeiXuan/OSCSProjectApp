package log;

import java.io.File;
import java.util.Base64;
import java.util.Scanner;

import org.ehcache.Cache;

import logExtra.Client;
import logExtra.KeyPairGen;
import login.controller.LoginPageController;

public class LogModelThread implements Runnable{
	private Client client;
	private String logName;
	private volatile boolean running;
	private volatile boolean sleeping;
	private String username;
	
	public LogModelThread(String logName) {
		this.client = new Client(logName);
		this.logName = logName;
		running = true;
		sleeping = false;
		this.username = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class).get("User");
	}
	
	@Override
	public void run() {
		while(running) {
			while(!sleeping) {
				// Update event log
				// Encrypt
				LogModelModel.updateOrGetLogs(logName);
				
				// Find someone
				client.getListOfAvailableNetwork();
				
				// Sign (private key) and find its signed file's hash // Check for private public key
				// Create Transcation new Transcation("UserFROM", "UserTO", EncryptedFile, signedFilesHash);
				// Get previous block hash
				// Create Block new Block(Transcation, previousBlock/Hash);
				// Mine (2 mins)
				
				// Send
				File file = LogModelModel.getFile(logName);
				String signature = Base64.getEncoder().encodeToString(KeyPairGen.signFile(file, username));
				client.sendFileTransferRequest(username, signature);
				
				// Set sleep = true;
				sleeping = true;
			}
			// Sleep for 3 mins
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Method to stop the thread
	public synchronized void stop() {
		running = false;
	}
	
	public void requestFileTransferRequest() {
		client.sendFileTransferRequest(logName, username);
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
			System.out.println("\t3. EXIT.");
			
			int select = sc.nextInt();
			System.out.println("");
			switch(select) {
				case 1:
					client.getListOfAvailableNetwork();
					break;
				case 2:
					client.sendFileTransferRequest("Application", "THISWONTWORKANYMORE");
					break;
				case 3:
					on = false;
				default:
					on = false;
			}
		}
		sc.close();
		client.close();
	}
}
