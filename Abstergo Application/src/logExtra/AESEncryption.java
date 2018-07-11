package logExtra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

public class AESEncryption {
	private byte[] key;
	private byte[] iv;

	public AESEncryption() {
		key = generateKey();
		iv = generateIV();
	}

	public AESEncryption(byte[] key) {
		this.key = key.clone();
		this.iv = generateIV();
	}

	public AESEncryption(byte[] key, byte[] iv) {
		this.key = key.clone();
		this.iv = iv.clone();
	}

	private byte[] generateKey() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] key = new byte[16];
		secureRandom.nextBytes(key);

		return key;
	}

	private byte[] generateIV() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] iv = new byte[12];
		secureRandom.nextBytes(iv);

		return iv;
	}

	private Cipher getEncryptCipher() {
		try {
			final Cipher CIPHER = Cipher.getInstance("AES/GCM/NoPadding");
			CIPHER.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));

			/*
			 * byte[] encrypted = CIPHER.doFinal(value.getBytes("UTF-8"));
			 * 
			 * ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length +
			 * encrypted.length); byteBuffer.putInt(iv.length); byteBuffer.put(iv);
			 * byteBuffer.put(encrypted); byte[] cipherMessage = byteBuffer.array();
			 * 
			 * String encryptedText = Base64.getEncoder().encodeToString(cipherMessage);
			 */

			return CIPHER;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Cipher getDecryptCipher() {
		try {
			/*
			 * byte[] decoded = Base64.getDecoder().decode(encrypted);
			 * 
			 * ByteBuffer byteBuffer = ByteBuffer.wrap(decoded); int ivLength =
			 * byteBuffer.getInt(); byte[] iv = new byte[ivLength]; byteBuffer.get(iv);
			 * byte[] cipherText = new byte[byteBuffer.remaining()];
			 * byteBuffer.get(cipherText);
			 */

			final Cipher CIPHER = Cipher.getInstance("AES/GCM/NoPadding");
			CIPHER.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(128, iv));

			// byte[] original = CIPHER.doFinal(cipherText);
			// String decryptedText = new String(original);

			return CIPHER;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public File encryptFile(File file) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		CipherOutputStream cos = null;
		try {
			String fileName = file.getName();
			File output = File.createTempFile(fileName.substring(0, fileName.length() - 4), "AES.txt");
			output.deleteOnExit();
			fis = new FileInputStream(file);
			fos = new FileOutputStream(output);
			cos = new CipherOutputStream(fos, getEncryptCipher());

			fos.write(iv); // Writing iv to file
			byte[] data = new byte[1024];
			int read = fis.read(data);
			while (read != -1) {
				cos.write(data, 0, read);
				read = fis.read(data);
			}

			return output;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (cos != null) {
					cos.flush();
					cos.close();
				}
				if (fos != null) {
					fos.flush();
					fos.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public File decryptFile(File file) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		CipherOutputStream cos = null;
		try {
			File output = File.createTempFile(file.getName(), "DECRYPTED.txt");
			output.deleteOnExit();
			fis = new FileInputStream(file);
			fos = new FileOutputStream(output);

			byte[] ivBuffer = new byte[12]; // Reading iv
			int read = fis.read(ivBuffer, 0, 12);
			this.iv = ivBuffer.clone(); // Getting iv from file and setting it
			if (read != -1) {
				cos = new CipherOutputStream(fos, getDecryptCipher());

				byte[] data = new byte[1024];
				int read2 = fis.read(data);
				while (read2 != -1) {
					cos.write(data, 0, read2);
					read2 = fis.read(data);
				}
			}
			return output;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (cos != null) {
					cos.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public void clearMemory() {
		Arrays.fill(key, (byte) 0);
	}

	/*
	 * public static void main(String[] args) throws IOException { AESEncryption aes
	 * = new AESEncryption();
	 * System.out.println(Base64.getEncoder().encodeToString(aes.iv));
	 * System.out.println(Base64.getEncoder().encodeToString(aes.key));
	 * //aes.encryptFile(new File("ApplicationAES.txt"));
	 * 
	 * Scanner sc = new Scanner(System.in); sc.nextLine(); sc.close();
	 * 
	 * File file = aes.decryptFile(new File("ApplicationAES.txt"));
	 * writeToFile(file, "ApplicationDECRYPTED.txt"); //
	 * System.out.println("Encrypted String: " + encryptedString); //
	 * System.out.println("Decrypted String: " + decryptedString);
	 * 
	 * aes.clearMemory(); }
	 */
}
