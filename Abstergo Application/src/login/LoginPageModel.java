package login;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class LoginPageModel {
	private String username;
	private String password;

	protected LoginPageModel() {
		username = null;
		password = null;
	}

	protected LoginPageModel(String username, String password) {
		this.username = username;
		this.password = password;
	}

	protected String hashPassword() {
		Argon2 argon2 = Argon2Factory.create();
		// Parsing String to char[]
		char[] passwordChar = password.toCharArray();
		String hash = "";
		try {
			hash = argon2.hash(45, 65536, 1, passwordChar); // Hash password (Max 3 secs)
			// int iterations = Argon2Helper.findIterations(argon2, 3000, 65536, 1);
			// System.out.println("Optimal number of iterations: " + hash);
		} finally {
			// Wipe confidential data
			argon2.wipeArray(passwordChar);
		}
		return hash;
	}
	
	protected boolean verifyPassword(String hash) {
		Argon2 argon2 = Argon2Factory.create();
		
		if (argon2.verify(hash, password)) {
			return true; // Hash matches password
		} else {
			return false; // Hash doesn't match password
		}
	}
	
	protected boolean validateAccount() {
		/*
		 * //Calls DAO (Azure) for database 
		 * retrievedPassword = LoginDAO.getPassword(username);
		 * return verifyPassword(retrievedPassword);
		 */
		
		return false;
	}

	private int getPasswordComplexity() {
		if (password.length() < 8 && password.length() > 36) {
			return 1;
		}

		if (password.contains(" ")) {
			return 2;
		} else {
			String regex = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\W])(?=\\S+$).{8,36}";
			boolean whether = password.matches(regex);

			if (!whether) {
				return 3;
			}
		}

		return 0;
	}

	protected String checkPasswordComplexity() {
		switch (getPasswordComplexity()) {
		case 0:
			return "";
		case 1:
			return "Password must be 8 to 36 letters.";
		case 2:
			return "Password cannot contain spaces.";
		case 3:
			return "Password must contains at least one lowercase letter, uppercase letter, symbol and number.";
		default:
			return "Unexpected error.";
		}
	}

	/*
	public static void main(String[] args) {
		String username = "Testing";
		String password = "ABCabc123!@#";

		LoginPageModel model = new LoginPageModel(username, password);
		String hash = model.hashPassword();
		String hashByte = hash.getBytes(); (97 Bytes)
		System.out.println("Password hash: " + hash);
		System.out.println("Is password correct? -> " + model.verifyPassword(hash));
		if (model.checkPasswordComplexity().isEmpty()) {
			System.out.println("Password changed successfully.");
		} else {
			System.out.println(model.checkPasswordComplexity());
		}
	}
	*/
}
