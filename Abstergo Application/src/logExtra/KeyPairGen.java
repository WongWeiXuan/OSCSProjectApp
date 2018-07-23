package logExtra;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.apache.commons.io.FileUtils;

import log.LogDAO;

public class KeyPairGen {
	private KeyPairGenerator keyGen;
	private KeyPair keyPair;
	
	public KeyPairGen() {
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			keyPair = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getPublicKeyByte() {
		return keyPair.getPublic().getEncoded();
	}
	
	public byte[] getPrivateKeyByte() {
		return keyPair.getPrivate().getEncoded();
	}
	
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}
	
	public PrivateKey getPrivateKey() {
		return keyPair.getPrivate();
	}
	
	public static byte[] signFile(File toBeSigned, PrivateKey privateKey) {
		try {
			Signature rsa = Signature.getInstance("SHA256withRSA");
			rsa.initSign(privateKey);
			rsa.update(FileUtils.readFileToByteArray(toBeSigned));
			return rsa.sign();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] signFile(File toBeSigned, String username) {
		try {
			// Get public key from db
			PrivateKey privateKey = LogDAO.getPrivateKey(username);
			
			// Sign File
			Signature rsa = Signature.getInstance("SHA256withRSA");
			rsa.initSign(privateKey);
			rsa.update(FileUtils.readFileToByteArray(toBeSigned));
			return rsa.sign();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static boolean verify(File toBeVerified, PublicKey publicKey, byte[] signature) {
		try {
			Signature rsa = Signature.getInstance("SHA256withRSA");
			rsa.initVerify(publicKey);
			rsa.update(FileUtils.readFileToByteArray(toBeVerified));
			return rsa.verify(signature);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean verify(File toBeVerified, String username, byte[] signature) {
		try {
			// Get public key from db
			PublicKey publicKey = LogDAO.getPublicKey(username);
			
			// Verify signature
			Signature rsa = Signature.getInstance("SHA256withRSA");
			rsa.initVerify(publicKey);
			rsa.update(FileUtils.readFileToByteArray(toBeVerified));
			return rsa.verify(signature);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static void main(String[] arg0) throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException {
		File file = new File("src/resource/logs/ApplicationLog2.txt");
		
		byte[] signature = KeyPairGen.signFile(file, LogDAO.getPrivateKey("wongweixuan3@gmail.com"));
		if(KeyPairGen.verify(file, LogDAO.getPublicKey("wongweixuan3@gmail.com"), signature)) {
			System.out.println("Verified");
		} else {
			System.out.println("Unverified");
		}
	}
}
