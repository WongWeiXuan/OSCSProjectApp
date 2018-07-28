package AbstergoREST.main.model;

public class BluetoothDevice {
	private String email;
	private String bluetoothAddress;
	private String friendlyName;
	private String majorClass;

	public BluetoothDevice(String email, String bluetoothAddress, String friendlyName, String majorClass) {
		this.email = email;
		this.bluetoothAddress = bluetoothAddress;
		this.friendlyName = friendlyName;
		this.majorClass = majorClass;
	}

	public String getEmail() {
		return email;
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
}
