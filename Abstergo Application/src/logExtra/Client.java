package logExtra;

public class Client {
	public HandshakeThread handshake;
	private Thread thread;

	public Client(String logName) {
		handshake = new HandshakeThread();
		thread = new Thread(handshake);
		thread.start();
	}

	public void getListOfAvailableNetwork() {
		HandshakeThread.sendPacket();
	}
	
	public void sendFileTransferRequest(String logName, String username, String signature) {
		getListOfAvailableNetwork();
		handshake.sendFileTransferRequest(logName, username, signature);
	}
	
	public void requestFileTransferRequest(String logName) {
		getListOfAvailableNetwork();
		handshake.requestFileTransferRequest(logName);
	}
	
	@SuppressWarnings("deprecation")
	public void close() {
		handshake.stop();
		thread.stop();
	}
}
