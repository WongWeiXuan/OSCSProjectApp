package log;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DiscoveryThread implements Runnable {

	@Override
	public void run() {
		DatagramSocket datagramSocket = null;
		try {
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
					// Give it an official status
					// TODO
				} else if ("FILETRANSFER".equals(packetData)) {
					// ADDESS == FRIEND ? ACCEPT : DENY
					// Establish to tcp
					// TODO

					// Upon accept
					ServerSocket serverSocket = new ServerSocket(9653);
					Socket socket = serverSocket.accept();
					Syket.receive(packet); // Waits for any client connection

				// Upon receiving packet
				String packetData = new String(packet.stem.out.println("Accepted connection : " + socket);
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

					BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("copy.txt"));
					outputStream.write(fileBuffer, 0, currentTotal);
					outputStream.flush();
					outputStream.close();
					socket.close();
					System.out.println("File transfer complete");
					serverSocket.close();
					// Write to transaction
					// TODO
					// Retrieve array of Blocks
					// Check (ket.receive(packet); // Waits for any client connection

				// Upon receiving packet
				String packetData = new String(packet.integrity of) existing blocks
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
eam(new FileOutputStream("copy.txt"));
					outputStream.write(fileBuffer, 0, currentTotal);
					outputStream.flush();
					outputStream.close();
					socket.close();
					System.out.println("File transfer complete");
					serverSocket.close();
					// Write to transaction
					// TODO
					// Retrieve array of Blocks
					// Check (ket.receive(packet); // Waits for any client connection

				// Upon receiving packet
				String packetData = new String(packet.integrity of) existing blocks
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
eam(new FileOutputStream("copy.txt"));
					outputStream.write(fileBuffer, 0, currentTotal);
					outputStream.flush();
					outputStream.close();
					socket.close();
					System.out.println("File transfer complete");
					serverSocket.close();
					// Write to transaction
					// TODO
					// Retrieve array of Blocks
					// Check (ket.receive(packet); // Waits for any client connection

				// Upon receiving packet
				String packetData = new String(packet.