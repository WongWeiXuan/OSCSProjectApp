package logExtra;

public class Client {
	private HandshakeThread handshake;
	private String logName;

	public Client(String logName) {
		handshake = new HandshakeThread();
		Thread thread = new Thread(handshake);
		thread.start();
		this.logName = logName;
	}

	public void getListOfAvailableNetwork() {
		HandshakeThread.sendPacket();
	}
	
	public void sendFileTransferRequest(String username, String signature) {
		handshake.sendFileTransferRequest(logName, username, signature);
	}
	
	public void requestFileTransferRequest() {
		handshake.requestFileTransferRequest(logName);
	}
	
	public void close() {
		handshake.stop();
	}
}
