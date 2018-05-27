package login;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class LoginPageModel {
	private String email;
	private String password;
	private byte[] salt;

	protected LoginPageModel() {
		email = null;
		password = null;
		salt = null;
	}

	public LoginPageModel(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}
	
	public LoginPageModel(String email, String password, byte[] salt) {
		super();
		this.email = email;
		this.password = password;
		this.salt = salt;
	}

	public void generateSalt() throws NoSuchAlgorithmException {
		final SecureRandom SR = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[128];
		SR.nextBytes(salt);

		this.salt = salt;
	}

	public byte[] encodeHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
		if(salt == null) {
			generateSalt();
		}
		char[] passwordChar = password.toCharArray();
		PBEKeySpec spec = new PBEKeySpec(passwordChar, salt, 200000, 64 * 16);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();

		return hash;
	}

	protected boolean verifyPassword(byte[] hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] enteredHash = encodeHashPassword();
		int diff = hash.length ^ enteredHash.length;
		for(int i = 0; i < hash.length && i < enteredHash.length; i++) {
			diff |= hash[i] ^ enteredHash[i];
		}
		
		return diff == 0;
	}

	protected boolean validateAccount() {
		/*
		 * //Calls DAO (Azure) for database retrievedPassword =
		 * LoginDAO.getPassword(email); return verifyPassword(retrievedPassword);
		 */

		return false;
	}

	private static int getPasswordComplexity(String password) {
		if (password.length() < 12) {
			return 1;
		}

		if (password.contains(" ")) {
			return 2;
		} else {
			String regex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\W])(?=\\S+$).{12,}";
			boolean whether = password.matches(regex);

			if (!whether) {
				return 3;
			}
		}

		return 0;
	}

	public static String checkPasswordComplexity(String password) {
		switch (getPasswordComplexity(password)) {
		case 0:
			return "Pass";
		case 1:
			return "Password must be at least 12 letters.";
		case 2:
			return "Password cannot contain spaces.";
		case 3:
			return "Password must contains at least one lowercase letter,\n uppercase letter,\n symbol and number.";
		default:
			return "Unexpected error.";
		}
	}

	/*
	public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
		String email = "Testing";
		String password = "ABCabc123!@#";

		LoginPageModel model = new LoginPageModel(email, password);
		
		byte[] hash = model.encodeHashPassword();
		System.out.println("Password hash: " + Base64.getEncoder().encodeToString(hash));
		System.out.println("Is password correct? -> " + model.verifyPassword(hash));
		
		String result = LoginPageModel.checkPasswordComplexity(password);
		
		if ("Pass".equals(result)) {
			System.out.println("Password changed successfully.");
		} else {
			System.out.println(result);
		}
	}
	*/
}
