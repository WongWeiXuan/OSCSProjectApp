package AbstergoREST.main.model;

public class Email {
	String username;
	String address;
	String password;

	public Email() {}

	public Email(String username, String address, String password) {
		this.username = username;
		this.address = address;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
