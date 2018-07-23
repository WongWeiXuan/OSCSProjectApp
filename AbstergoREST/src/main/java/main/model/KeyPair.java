package main.model;

public class KeyPair {
	private String email;
	private String privateKey;
	private String publicKey;
	private String encryptionKey;
	
	public KeyPair() {}
	
	public KeyPair(String email, String privateKey, String publicKey, String encryptionKey) {
		this.email = email;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.encryptionKey = encryptionKey;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
}
