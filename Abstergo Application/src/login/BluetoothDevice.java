package login;

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
}
