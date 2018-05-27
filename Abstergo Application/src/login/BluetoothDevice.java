package login;

public class BluetoothDevice {
	private String bluetoothAddress;
	private String friendlyName;
	private int majorClass;

	public BluetoothDevice(String bluetoothAddress, String friendlyName, int majorClass) {
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

	public int getMajorClass() {
		return majorClass;
	}
}
