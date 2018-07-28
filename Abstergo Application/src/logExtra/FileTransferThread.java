package logExtra;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

import log.LogDAO;
import log.LogModelThread;
import login.controller.LoginPageController;

public class FileTransferThread implements Runnable{
	private volatile boolean running;
	private Connection c;
	private String logName;
	private String userName;
	private byte[] signature;
	private boolean request;
	
	public FileTransferThread(Connection c, String logName, String userName, byte[] signature, boolean request) {
		this.c = c;
		this.logName = logName;
		this.userName = userName;
		this.signature = signature;
		this.request = request;
	}
	
	@Override
	public void run() {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(9653);
			Socket socket;
			do {
				socket = serverSocket.accept();
				System.out.println("ADDRESS: " + socket.getInetAddress().getHostAddress());
			} while(socket.getInetAddress().getHostAddress() == c.getAddress().getHostAddress() && running);
			
			System.out.println("Accepted connection : " + socket);
			byte[] fileBuffer = new byte[10000000];
	
			InputStream inputStream = socket.getInputStream();
			int bytesRead = 0;
			int currentTotal = bytesRead;
	
			do {
				bytesRead = inputStream.read(fileBuffer, currentTotal, (fileBuffer.length - currentTotal));
				if (bytesRead >= 0) {
					currentTotal += bytesRead;
				}
			} while (bytesRead > 0);
			
			// The file that needs to save
			File file = File.createTempFile("copy", ".txt");
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
			outputStream.write(fileBuffer, 0, currentTotal);
			outputStream.flush();
			outputStream.close();
			socket.close();
			System.out.println("File transfer complete");
			serverSocket.close();
			if(KeyPairGen.verify(file, userName, signature)) {
				System.out.println("SUCCESSFUL FILE VERIFICATION!");
				if(request) {
					LogDAO.writeToFile(file, "src/resource/logs/" + logName + "Log.txt");
				} else {
					LogDAO.writeToFile(file, "src/resource/logs/" + logName + "/" + c.getAddress() + ".txt");
				}
				
				// Write to transaction
				// Retrieve array of Blocks
				// Check (integrity of) existing blocks
				// Write new block
				// Add new block to array
				// Mine block
				// Store/Upload block array
				if(!request) {
					BlockChain blockChain = new BlockChain(logName, socket.getInetAddress().getHostAddress(), true);
					if(blockChain.isChainValid()) {
						Transcation trans = new Transcation(socket.getInetAddress().getHostAddress(), socket.getLocalAddress().getHostAddress(), file, true);
						blockChain.addBlock(trans);
					} else {
						// Get the other person's blockchain to check...
						// IF fails
						file.delete();
						System.out.println("Integrity check Failed. File deleted");
						// ReInitialise the file transfer with another person again
					}
				}
			} else {
				System.out.println("FILE VERIFICATION FAILED!");
				file.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		HandshakeThread.request = false;
	}
}
