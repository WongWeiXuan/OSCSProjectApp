package logExtra;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import log.LogDAO;

public class FileTransferThread implements Runnable{
	private volatile boolean running;
	private Connection c;
	private String logName;
	private String userName;
	private byte[] signature;
	
	public FileTransferThread(Connection c, String logName, String userName, byte[] signature) {
		this.c = c;
		this.logName = logName;
		this.userName = userName;
		this.signature = signature;
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
			byte[] fileBuffer = new byte[1500000]; // TODO Change file size
	
			InputStream inputStream = socket.getInputStream();
			int bytesRead = inputStream.read(fileBuffer, 0, fileBuffer.length);
			int currentTotal = bytesRead;
	
			do {
				bytesRead = inputStream.read(fileBuffer, currentTotal, (fileBuffer.length - currentTotal));
				if (bytesRead >= 0) {
					currentTotal += bytesRead;
				}
			} while (bytesRead > -1);
	
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
				LogDAO.writeToFile(file, "src/resource/logs/" + logName + "/" + c.getAddress() + ".txt");
			}
			// Write to transaction
			// TODO
			// Retrieve array of Blocks
			// Check (integrity of) existing blocks
			// Write new block
			// Add new block to array
			// Mine block
			// Store/Upload block array
			BlockChain blockChain = new BlockChain(logName, true);
			if(blockChain.isChainValid()) {
				Transcation trans = new Transcation(socket.getInetAddress().getHostAddress(), socket.getLocalAddress().getHostAddress(), file);
				blockChain.addBlock(trans);
			} else {
				// Get the other person's blockchain to check...
				// IF fails
				file.delete();
				System.out.println("Integrity check Failed. File deleted");
				// ReInitialise the file transfer with another person again
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
