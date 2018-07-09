package logExtra;

public class Connection {
	final private int SEARCHING = 0x01;
	final private int SYN = 0x02;
	final private int SYNACK = 0x03;
	final private int ACK = 0x04;
	final private int FILETRANSFER = 0x05;
	final private int LISTENING = 0x06;
	private String address;
	private int status;
	private boolean valid;
	
	public Connection() {
		this.valid = false;
	}
	
	public Connection(String address, int status) {
		super();
		this.address = address;
		this.status = status;
	}
	
	public String getAddress() {
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
