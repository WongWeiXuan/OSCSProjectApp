package AbstergoREST.main.model;

public class Profile {
	private String email;
	private String password;
	private String salt;
	private String bluetoothAddress;
	private String friendlyName;
	private String majorClass;
	
	public Profile() {
		super();
	}

	public Profile(String email, String password, String salt, String bluetoothAddress, String friendlyName,
			String majorClass) {
		super();
		this.email = email;
		this.password = password;
		this.salt = salt;
		this.bluetoothAddress = bluetoothAddress;
		this.friendlyName = friendlyName;
		this.majorClass = majorClass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getBluetoothAddress() {
		return bluetoothAddress;
	}

	public void setBluetoothAddress(String bluetoothAddress) {
		this.bluetoothAddress = bluetoothAddress;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getMajorClass() {
		return majorClass;
	}

	public void setMajorClass(String majorClass) {
		this.majorClass = majorClass;
	}
}