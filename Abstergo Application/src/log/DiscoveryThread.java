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
				String packetData = new String(packet.getData()).trim();
				System.out.println("Packet received from: " + packet.getAddress().getHostAddress());
				System.out.println("Data: " + packetData);

				// Process Message
				if ("YOUALIVE?".equals(packetData)) {
					// Send response
					byte[] responseBuffer = new byte[10000];
					responseBuffer = "YEA,I'MALIVE.HOWABOUTYOU?".getBytes();
					DatagramPacket sendPacket = new DatagramPacket(responseBuffer, responseBuffer.length,
							packet.getAddress(), packet.getPort());
					datagramSocket.send(sendPacket);
				} else if ("IMFINETHANKYOU".equals(packetData)) {
					// Give it an official status
					// TODO
				} else if ("FILETRANSFER".equals(packetData)) {
					// ADDESS == FRIEND ? ACCEPT : DENY
					// Establish to tcp
					//TODO
					
					// Upon accept
					ServerSocket serverSocket = new ServerSocket(9653);
					Socket socket = serverSocket.accept();
					System.out.println("Accepted connection : " + socket);
					byte[] fileBuffer = new byte[1500000]; //TODO Change file size

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
