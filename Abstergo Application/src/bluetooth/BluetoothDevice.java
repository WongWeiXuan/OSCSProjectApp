package bluetooth;

import login.LoginDAO;
import login.LoginPageModel;

public class BluetoothDevice {
	private String bluetoothAddress;
	private String friendlyName;
	private String majorClass;

	public BluetoothDevice(String bluetoothAddress, String friendlyName, String majorClass) {
		this.bluetoothAddress = bluetoothAddress;
		this.friendlyName = friendlyName;
		this.majorClass = majorClass;
	}

	public String getBluetoothAddress() {
		return bluetoothAddress;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getMajorClass() {
		return majorClass;
	}

	public static void createLogin(LoginPageModel login, BluetoothDevice device) {
		LoginDAO.createLogin(login, device);
	}
}
