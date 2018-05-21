package login;

public class BluetoothDevice {
	private String bluetoothAddress;
	private String friendlyName;
	
	public BluetoothDevice(String BluetoothAddress, String FriendlyName) {
		this.bluetoothAddress = BluetoothAddress;
		this.friendlyName = FriendlyName;
	}
	
	public String getBluetoothAddress() {
		return bluetoothAddress;
	}
	public String getFriendlyName() {
		return friendlyName;
	}
}
