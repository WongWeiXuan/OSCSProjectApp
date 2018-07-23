package logExtra;

import java.net.InetAddress;

public class Connection {
	final public int SEARCHING = 1;
	final public int SYN = 2;
	final public int SYNACK = 3;
	final public int ACK = 4;
	final public int FILETRANSFER = 5;
	final public int LISTENING = 6;
	private InetAddress address;
	private int status;
	private boolean valid;
	
	public Connection() {
		this.valid = false;
	}
	
	public Connection(InetAddress address, int status) {
		super();
		this.address = address;
		this.status = status;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public void getStatus() {
		switch(status) {
			case SEARCHING:
				System.out.println("Searching for host...");
				break;
			case SYN:
				System.out.println("Sending 'SYN'...");
				break;
			case SYNACK:
				System.out.println("Sending 'SYNACK'...");
				break;
			case ACK:
				System.out.println("Sending 'ACK'...");
				break;
			case FILETRANSFER:
				System.out.println("Establishing ft...");
				break;
			case LISTENING:
				System.out.println("Waiting...");
				break;
		}
	}
	
	public int getStatusInt() {
		return status;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
