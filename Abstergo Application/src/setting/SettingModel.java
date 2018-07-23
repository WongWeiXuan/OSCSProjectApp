package setting;

import java.util.ArrayList;
import java.util.Scanner;

public class SettingModel {
	private ArrayList<String> logFile;
	private ArrayList<String> logDisplay;
	private boolean incomingAccept;
	private long incomingMaximum;
	private String onDiconnection;
	private int timeout;

	public SettingModel() {
	}

	public SettingModel(ArrayList<String> logFile, ArrayList<String> logDisplay, boolean incomingAccept,
			long incomingMaximum, String onDiconnection, int timeout) {
		this.logFile = logFile;
		this.logDisplay = logDisplay;
		this.incomingAccept = incomingAccept;
		this.incomingMaximum = incomingMaximum;
		this.onDiconnection = onDiconnection;
		this.timeout = timeout;
	}

	public ArrayList<String> getLogFile() {
		return logFile;
	}

	public String getLogFileString() {
		boolean first = true;

		StringBuffer buffer = new StringBuffer();
		for (String s : logFile) {
			if (first) {
				buffer.append(s);
				first = false;
			} else {
				buffer.append(";" + s);
			}
		}
		return buffer.toString();
	}

	public ArrayList<String> getLogDisplay() {
		return logDisplay;
	}

	public String getLogDisplayString() {
		boolean first = true;

		StringBuffer buffer = new StringBuffer();
		for (String s : logDisplay) {
			if (first) {
				buffer.append(s);
				first = false;
			} else {
				buffer.append(";" + s);
			}
		}
		return buffer.toString();
	}

	public boolean isIncomingAccept() {
		return incomingAccept;
	}

	public long getIncomingMaximum() {
		return incomingMaximum;
	}

	public String getOnDiconnection() {
		return onDiconnection;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setLogFile(ArrayList<String> logFile) {
		this.logFile = logFile;
	}

	public void setLogFile(String logFile) {
		ArrayList<String> returnArray = new ArrayList<String>();
		Scanner sc = new Scanner(logFile);
		sc.useDelimiter(";");

		while (sc.hasNext()) {
			returnArray.add(sc.next());
		}
		sc.close();

		this.logFile = returnArray;
	}

	public void setLogDisplay(ArrayList<String> logDisplay) {
		this.logDisplay = logDisplay;
	}

	public void setLogDisplay(String logDisplay) {
		ArrayList<String> returnArray = new ArrayList<String>();
		Scanner sc = new Scanner(logDisplay);
		sc.useDelimiter(";");

		while (sc.hasNext()) {
			returnArray.add(sc.next());
		}
		sc.close();

		this.logDisplay = returnArray;
	}

	public void setIncomingAccept(boolean incomingAccept) {
		this.incomingAccept = incomingAccept;
	}

	public void setIncomingMaximum(long incomingMaximum) {
		this.incomingMaximum = incomingMaximum;
	}

	public void setOnDiconnection(String onDiconnection) {
		this.onDiconnection = onDiconnection;
	}

	public void setTimeout(int timeout) {
		if(timeout > 0) {
			timeout *= 60 * 1000;
		}
		this.timeout = timeout;
	}

	public int preProcess(int column, String name) {
		switch (column) {
		case 0:
			if ("Logout".equals(name)) {
				return 0;
			} else if ("Signout".equals(name)) {
				return 1;
			} else if ("Sleep".equals(name)) {
				return 2;
			} else if ("Shutdown".equals(name)) {
				return 3;
			} else {
				return -1;
			}
		case 1:
			if ("0".equals(name)) {
				return 0;
			} else if ("15".equals(name)) {
				return 1;
			} else if ("30".equals(name)) {
				return 2;
			} else if ("60".equals(name)) {
				return 3;
			} else if ("120".equals(name)) {
				return 4;
			} else if ("-1".equals(name)) {
				return 5;
			} else {
				return -1;
			}
		default:
			return -1;
		}
	}

	@Override
	public String toString() {
		return "SettingModel [logFile=" + logFile + ", logDisplay=" + logDisplay + ", incomingAccept=" + incomingAccept
				+ ", incomingMaximum=" + incomingMaximum + ", onDiconnection=" + onDiconnection + "]";
	}
}
