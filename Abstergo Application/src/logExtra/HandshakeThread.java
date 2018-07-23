package logExtra;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.ehcache.Cache;

import login.controller.LoginPageController;

public class HandshakeThread implements Runnable {
	public static Map<String, Connection> connectionList;
	private volatile boolean running;
	private static String logName;
	private String userName;
	private static DatagramSocket socket;
	private static boolean broadcasted;

	public HandshakeThread() {
		connectionList = new HashMap<String, Connection>();
		running = true;
		logName = null;
		socket = null;
		broadcasted = false;
	}

	@Override
	public void run() {
		while (running) {
			try {
				socket = new DatagramSocket(9653, InetAddress.getByName("0.0.0.0"));
				socket.setBroadcast(true);

				// Receive a packet
				byte[] buffer = new byte[10000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet); // Waits for any client connection

				// Upon receiving packet
				String packetData = new String(packet.getData(), StandardCharsets.UTF_8).trim();
				String packetAddress = packet.getAddress().getHostAddress();

				if (InetAddress.getLocalHost().getHostAddress().equals(packetAddress)) {
					continue;
				}

				Connection c;
				if (!connectionList.containsKey(packetAddress)) {
					c = new Connection(packet.getAddress(), 1);
					connectionList.put(packetAddress, c);
				} else {
					c = connectionList.get(packetAddress);
				}

				if ("YOUALIVE?".equals(packetData)) {
					System.out.println("SYN RECEIVED: " + packetAddress + ", " + c.getStatusInt());
					byte[] responseBuffer;

					// Setting SYN established
					c.setStatus(2);
					connectionList.replace(packetAddress, c);
					responseBuffer = "YEA,I'MALIVE.HOWABOUTYOU?".getBytes(StandardCharsets.UTF_8);

					// Send response
					DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
							packet.getAddress(), packet.getPort());
					socket.send(sendPacket);

					// Setting SYNACK Sent status
					c.setStatus(3);
					connectionList.replace(packetAddress, c);
				} else if ("YEA,I'MALIVE.HOWABOUTYOU?".equals(packetData)) {
					System.out.println("SYNACK RECEIVED: " + packetAddress + ", " + c.getStatusInt());
					byte[] responseBuffer;
					if (c.getStatusInt() == 2 || broadcasted) {
						// Setting SYNACK established
						c.setStatus(3);
						c.setValid(true);
						connectionList.replace(packetAddress, c);
						responseBuffer = "IMFINETHANKYOU".getBytes(StandardCharsets.UTF_8);
					} else {
						System.out.println("SENDING RESET");
						connectionList.remove(packetAddress);

						responseBuffer = "WHOAREYOU?".getBytes(StandardCharsets.UTF_8);
					}

					// Send response
					DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
							packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
				} else if ("IMFINETHANKYOU".equals(packetData)) {
					System.out.println("ACK RECEIVED: " + packetAddress + ", " + c.getStatusInt());
					if (c.getStatusInt() == 3) {
						// Setting ACK established
						c.setStatus(4);
						c.setValid(true);
						connectionList.replace(packetAddress, c);
					} else {
						System.out.println("SENDING RESET");
						connectionList.remove(packetAddress);

						// Send response
						byte[] responseBuffer = "WHOAREYOU?".getBytes(StandardCharsets.UTF_8);
						DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
								packet.getAddress(), packet.getPort());
						socket.send(sendPacket);
					}
				} else if (packetData.contains("FILETRANSFER:")) {
					System.out.println("FT REQUEST RECEIVED");
					byte[] responseBuffer = null;

					if (c.isValid()) {
						// Setting to ready for ft
						c.setStatus(5);
						connectionList.replace(packetAddress, c);
						// FileTransferThread
						Scanner sc = new Scanner(packetData);
						sc.useDelimiter(":");
						sc.next();
						logName = sc.next();
						userName = sc.next();
						byte[] signature = Base64.getDecoder().decode(sc.next());
						new Thread(new FileTransferThread(c, logName, userName, signature)).start();
						responseBuffer = "FILETRANSFERREADY".getBytes(StandardCharsets.UTF_8);
					} else {
						System.out.println("SENDING RESET");
						responseBuffer = "WHOAREYOU?".getBytes(StandardCharsets.UTF_8);
					}

					// Send response
					DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
							packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
				} else if (packetData.equals("FILETRANSFERREADY")) {
					System.out.println("FT CONNECTION RECEIVED");

					if (c.getStatusInt() == 5) {
						activateFileTransfer(packetAddress, 9653);
					} else {
						System.out.println("SENDING RESET");
						byte[] responseBuffer = "WHOAREYOU?".getBytes(StandardCharsets.UTF_8);
						// Send response
						DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
								packet.getAddress(), packet.getPort());
						socket.send(sendPacket);
					}
				} else if (packetData.contains("CANSENDMEFILE?:")) {
					System.out.println("REQUEST FOR FILE RECEIVED");

					if (c.isValid()) {
						String logFileName = packetData.substring(15);
						if(checkWhetherGotFile(packetAddress, logFileName)) {
							userName = LoginPageController.cacheManager.getCacheManager().getCache("user", String.class, String.class).get("User");
							String signature = Base64.getEncoder().encodeToString(KeyPairGen.signFile(getFile(packetAddress, logFileName), userName));
							sendFileTransferRequest(logName, userName, signature);
						} else {
							System.out.println("FILE NOT FOUND!");
							byte[] responseBuffer = "SORRYIDONTHAVE".getBytes(StandardCharsets.UTF_8);
							// Send response
							DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
									packet.getAddress(), packet.getPort());
							socket.send(sendPacket);
						}
					} else {
						System.out.println("SENDING RESET");
						byte[] responseBuffer = "WHOAREYOU?".getBytes(StandardCharsets.UTF_8);
						// Send response
						DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
								packet.getAddress(), packet.getPort());
						socket.send(sendPacket);
					}
				} else if ("WHOAREYOU?".equals(packetData)) {
					System.out.println("RESET RECEIVED");
					connectionList.remove(packetAddress);
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					socket.close();
				}
			}
		}
		stop();
	}

	private boolean checkWhetherGotFile(String address, String logFileName) {
		File file = new File("src/resource/logs/" + logFileName + "/" + address);
		if(file.exists()) { 
		   return true;
		} else {
			return false;
		}
	}
	
	private File getFile(String address, String logFileName) {
		return new File("src/resource/logs/" + logFileName + "/" + address + ".txt"); // TODO Double check the file path pls
	}

	private void activateFileTransfer(String address, int port) {
		try {
			Socket socket = new Socket(address, port);

			File file = new File("src/resource/logs/" + logName + "Log.txt");

			byte[] buffer = new byte[(int) file.length()];
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			inputStream.read(buffer, 0, buffer.length);

			System.out.println("Sending Files...");
			OutputStream os = socket.getOutputStream();
			os.write(buffer, 0, buffer.length);

			inputStream.close();
			os.flush();
			os.close();
			socket.close();
			System.out.println("File Sent");
			// Write to transaction
			// TODO
			// Retrieve array of Blocks
			// Check (integrity of) existing blocks
			// Write new block
			// Add new block to array
			// Mine block
			// Store/Upload block array
			BlockChain blockChain = new BlockChain(logName, false);
			if(blockChain.isChainValid()) {
				Transcation trans = new Transcation(socket.getInetAddress().getHostAddress(), socket.getLocalAddress().getHostAddress(), file);
				blockChain.addBlock(trans);
			} else {
				// IF fails
				// Ask file from someone
				requestFileTransferRequest(logName);
				System.out.println("Integrity check Failed. How is this possible?");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void sendFileTransferRequest(String logName1, String username, String signature) {
		try {
			logName = logName1;
			String line = "FILETRANSFER:" + logName + ":" + username + ":" + signature;
			byte[] responseBuffer = line.getBytes(StandardCharsets.UTF_8);
			if(connectionList.size() > 0) {
				for(Connection address : connectionList.values()) {
					if(address.isValid()) {
						DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length, address.getAddress(), 9653);
						socket.send(sendPacket);
						address.setStatus(5);
						connectionList.replace(address.getAddress().getHostAddress(), address);
					}
				}
			} else {
				// GET FROM DB
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void requestFileTransferRequest(String logName1) {
		try {
			logName = logName1;
			String line = "CANSENDMEFILE?:" + logName;
			byte[] responseBuffer = line.getBytes(StandardCharsets.UTF_8);
			if(connectionList.size() > 0) {
				for(Connection address : connectionList.values()) {
					if(address.isValid()) {
						DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length, address.getAddress(), 9653);
						socket.send(sendPacket);
						address.setStatus(5);
						connectionList.replace(address.getAddress().getHostAddress(), address);
					}
				}
			} else {
				// GET FROM DB
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static void sendPacket() {
		try {
			byte[] responseBuffer = "YOUALIVE?".getBytes(StandardCharsets.UTF_8);

			// Broadcast to all the network interfaces
			Enumeration<NetworkInterface> networkInterfacesEnum = NetworkInterface.getNetworkInterfaces();
			while (networkInterfacesEnum.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfacesEnum.nextElement();

				if (!networkInterface.isUp() || networkInterface.isLoopback()) {
					continue;
				} else {
					for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
						InetAddress broadcast = interfaceAddress.getBroadcast();
						if (broadcast != null) {
							// Send the broadcast package!
							DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
									broadcast, 9653);
							socket.send(sendPacket);
							broadcasted = true;
							System.out.println("Request packet sent to: " + broadcast.getHostAddress() + "; Interface: "
									+ networkInterface.getDisplayName());
						} else {
							continue;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLogName(String logName) {
		HandshakeThread.logName = logName;
	}

	// Method to stop the thread
	public synchronized void stop() {
		running = false;
		socket.close();
	}
}
