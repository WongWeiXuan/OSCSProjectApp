package logExtra;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DiscoveryThread implements Runnable {
	private ArrayList<Connection> connectionList;
	
	private Connection getConnection(DatagramPacket packet) {
		for(Connection c : connectionList) {
			if(c.getAddress().equals(packet.getAddress().getHostAddress())) {
				return c;
			}
		}
		
		return null;
	}
	
	private boolean isConnectionExist(DatagramPacket packet) {
		for(Connection c : connectionList) {
			if(c.getAddress().equals(packet.getAddress().getHostAddress())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void run() {
		DatagramSocket datagramSocket = null;
		try {
			connectionList = new ArrayList<Connection>();
			System.out.println("Listening on port \"9653\"...");
			// Keep a socket open to listen to all the UDP trafic that is destined for this
			// port
			datagramSocket = new DatagramSocket(9653, InetAddress.getByName("0.0.0.0"));
			datagramSocket.setBroadcast(true);

			while (true) {
				// Receive a packet
				byte[] buffer = new byte[10000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(packet); // Waits for any client connection

				// Upon receiving packet
				String packetData = new String(packet.getData(), StandardCharsets.UTF_8).trim();
				System.out.println("Packet received from: " + packet.getAddress().getHostAddress());
				System.out.println("Data: " + packetData);

				// Process Message
				if ("YOUALIVE?".equals(packetData)) {
					// Send response
					byte[] responseBuffer = "YEA,I'MALIVE.HOWABOUTYOU?".getBytes(StandardCharsets.UTF_8);
					DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
							packet.getAddress(), packet.getPort());
					datagramSocket.send(sendPacket);
				} else if ("IMFINETHANKYOU".equals(packetData)) {
					if(isConnectionExist(packet)) {
						getConnection(packet).setValid(true);;
					} else {
						// Send response
						byte[] responseBuffer = "WHOAREYOU?".getBytes(StandardCharsets.UTF_8);
						DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
								packet.getAddress(), packet.getPort());
						datagramSocket.send(sendPacket);
					}
				} else if ("FILETRANSFER".equals(packetData)) {
					if(getConnection(packet).isValid()) {
						// Make sure only one file trans is running
						// TODO
						
						// Establish to tcp
						// Upon accept
						ServerSocket serverSocket = new ServerSocket(9653);
						Socket socket = serverSocket.accept();
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
						File file = new File("copy.txt");
						BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
						outputStream.write(fileBuffer, 0, currentTotal);
						outputStream.flush();
						outputStream.close();
						socket.close();
						System.out.println("File transfer complete");
						serverSocket.close();
						// Write to transaction
						// TODO
						// Retrieve array of Blocks
						// Check (integrity of) existing blocks
						// Write new block
						// Add new block to array
						// Mine block
						// Store/Upload block array
						BlockChain blockChain = new BlockChain();
						if(blockChain.isChainValid()) {
							Transcation trans = new Transcation("UserFrom", "UserTo", file);
							blockChain.addBlock(trans);
						} else {
							// Get the other person's blockchain to check...
							// IF fails
							file.delete();
							System.out.println("Integrity check Failed. File deleted");
							// ReInitialise the file transfer with another person again
						}
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (datagramSocket != null) {
				datagramSocket.close();
			}
		}
	}
}
