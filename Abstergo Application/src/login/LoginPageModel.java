package login;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.google.gson.JsonObject;

import bluetooth.BluetoothDevice;
import bluetooth.LoginBluetoothModel;

public class LoginPageModel {
	private String email;
	private String password;
	private String salt;

	public LoginPageModel() {
		email = null;
		password = null;
		salt = null;
	}

	public LoginPageModel(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public LoginPageModel(String email, String password, String salt) {
		super();
		this.email = email;
		this.password = password;
		this.salt = salt;
	}

	public byte[] generateSalt() throws NoSuchAlgorithmException {
		final SecureRandom SR = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[128];
		SR.nextBytes(salt);

		return salt;
	}

	public byte[] encodeHashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
		salt = byteArrayToHexString(generateSalt());
		char[] passwordChar = password.toCharArray();
		PBEKeySpec spec = new PBEKeySpec(passwordChar, hexStringToByteArray(salt), 200000, 64 * 16);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return hash;
	}

	public byte[] encodeHashPassword(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		char[] passwordChar = password.toCharArray();
		PBEKeySpec spec = new PBEKeySpec(passwordChar, salt, 200000, 64 * 16);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		byte[] hash = skf.generateSecret(spec).getEncoded();

		return hash;
	}

	protected boolean verifyPassword(byte[] hash, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] enteredHash = encodeHashPassword(salt);
		int diff = hash.length ^ enteredHash.length;
		for (int i = 0; i < hash.length && i < enteredHash.length; i++) {
			diff |= hash[i] ^ enteredHash[i];
		}

		return diff == 0;
	}

	public boolean validateAccount() {
		try {
			JsonObject hash = LoginDAO.getLogin(email);
			byte[] dbPassword = hexStringToByteArray(hash.get("password").getAsString());
			byte[] dbSalt = hexStringToByteArray(hash.get("salt").getAsString());
			return verifyPassword(dbPassword, dbSalt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

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

	public static boolean checkWhetherEmailExist(String email) {
		return LoginDAO.getEmail(email);
	}

	public static BluetoothDevice getPairedDevice(String email) {
		JsonObject jObject = LoginDAO.getPairedDevice(email);
		return new BluetoothDevice(jObject.get("bluetoothAddress").getAsString(),
				jObject.get("friendlyName").getAsString(), jObject.get("majorClass").getAsString());
	}

	public static boolean checkPairedDevice(BluetoothDevice device) {
		try {
			ArrayList<BluetoothDevice> bluetoothArray = new ArrayList<BluetoothDevice>();
			bluetoothArray.add(device);
			LoginBluetoothModel.setPairedArray(bluetoothArray);
			return LoginBluetoothModel.scanForPairedBluetoothDevice();
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static String byteArrayToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X ", b));
		}
		return sb.toString().replace(" ", "");
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
