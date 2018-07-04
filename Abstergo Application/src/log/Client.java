package log;

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
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class Client {

	private static void sendPacket() {
		try {
			// Broadcast packets
			DatagramSocket broadcastSocket = new DatagramSocket();
			broadcastSocket.setBroadcast(true);

			byte[] sendData = "YOUALIVE?".getBytes(StandardCharsets.UTF_8);

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
							try {
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast,
										9653);
								broadcastSocket.send(sendPacket);
								System.out.println("Request packet sent to: " + broadcast.getHostAddress()
										+ "; Interface: " + networkInterface.getDisplayName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							continue;
						}
					}
				}
			}

			System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");

			// Wait for a response
			byte[] buffer = new byte[10000];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
			broadcastSocket.receive(receivePacket);

			// We have a response
			String packetData = new String(receivePacket.getData(), StandardCharsets.UTF_8).trim();
			System.out.println("Broadcast response from server: " + receivePacket.getAddress().getHostAddress() + ", "
					+ packetData);

			// Check if the message is correct
			if ("YEA,I'MALIVE.HOWABOUTYOU?".equals(packetData)) {
				// Send response
				byte[] responseBuffer = "IMFINETHANKYOU".getBytes(StandardCharsets.UTF_8);
				DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
						receivePacket.getAddress(), receivePacket.getPort());
				broadcastSocket.send(sendPacket);

				byte[] responseBuffer2 = "FILETRANSFER".getBytes(StandardCharsets.UTF_8);
				DatagramPacket sendPacket2 = new DatagramPacket(responseBuffer2, responseBuffer2.length,
						receivePacket.getAddress(), receivePacket.getPort());
				broadcastSocket.send(sendPacket2);
				activateFileTransfer(receivePacket.getAddress().getHostAddress(), receivePacket.getPort());
			}

			// Close the port!
			broadcastSocket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void activateFileTransfer(String address, int port) {
		try {
			Socket socket = new Socket(address, port);

			File file = new File("ApplicationAES.txt");

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

			// Craft planks
			// Craft crafting table
			// Craft pickaxe
			// Mine cobblestone
			// Mine iron ore
			// Craft furnace
			// Smelt iron ore
			// Craft iron sword
			// Craft iron pickaxe
			// Craft iron armour
			// Now you're ready

			// Store/Upload block array
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] arg0) throws InterruptedException {
		// Thread discoveryThread = new Thread(new DiscoveryThread());
		// discoveryThread.start();
		System.out.println("---------------------------");
		sendPacket();
	}
}
