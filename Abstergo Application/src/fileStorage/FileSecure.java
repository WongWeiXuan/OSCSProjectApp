package fileStorage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileSecure {
	private static final int ivSize = 16;
	private static final int keySize = 16;
	
	public static byte[] encrypt(byte[] bytes, String key) throws Exception {
		byte[] iv = new byte[ivSize];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(key.getBytes("UTF-8"));
        byte[] keyBytes = new byte[keySize];
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(bytes);
        
        byte[] encryptedIVAndFile = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndFile, 0, ivSize);
        System.arraycopy(encrypted, 0, encryptedIVAndFile, ivSize, encrypted.length);
        
        return encryptedIVAndFile;
	}
	
	public static byte[] decrypt(byte[] encryptedIvFileBytes, String key) throws Exception {
		byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvFileBytes, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        
        int encryptedSize = encryptedIvFileBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvFileBytes, ivSize, encryptedBytes, 0, encryptedSize);
        
        byte[] keyBytes = new byte[keySize];
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(key.getBytes("UTF-8"));
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);
        
        return decrypted;
	}
	
	public static byte[] hash(byte[] byteToHash) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(byteToHash);
	}
	
	public static byte[] getXORParBlock(byte[] b1, byte[] b2, byte[] b3, byte[] b4) {
		byte[] parBlock = null;
		
		byte[] oneAndTwo = new byte[Math.max(b1.length, b2.length)];
	    for (int i = 0; i < b1.length && i < b2.length; i++)
	        oneAndTwo[i] = (byte) (b1[i] ^ b2[i]);
	    for (int i = b2.length; i < b1.length; i++)
	        oneAndTwo[i] = b1[i];
	    for (int i = b1.length; i < b2.length; i++)
	        oneAndTwo[i] = b2[i];
	    int length = oneAndTwo.length;
	    while (length > 0 && oneAndTwo[length - 1] == 0)
	        length--;
	    if (length < oneAndTwo.length)
	        return Arrays.copyOf(oneAndTwo, length);
	    
	    if (b3 != null) {
	    	byte[] twoAndThree = new byte[Math.max(oneAndTwo.length, b3.length)];
		    for (int i = 0; i < oneAndTwo.length && i < b3.length; i++)
		    	twoAndThree[i] = (byte) (oneAndTwo[i] ^ b3[i]);
		    for (int i = b3.length; i < oneAndTwo.length; i++)
		    	twoAndThree[i] = oneAndTwo[i];
		    for (int i = oneAndTwo.length; i < b3.length; i++)
		    	twoAndThree[i] = b3[i];
		    int length2 = twoAndThree.length;
		    while (length2 > 0 && twoAndThree[length2 - 1] == 0)
		    	length2--;
		    if (length2 < twoAndThree.length)
		    	return Arrays.copyOf(twoAndThree, length2);
		    
		    if (b4 != null) {
		    	byte[] threeAndFour = new byte[Math.max(twoAndThree.length, b4.length)];
			    for (int i = 0; i < twoAndThree.length && i < b4.length; i++)
			    	threeAndFour[i] = (byte) (twoAndThree[i] ^ b4[i]);
			    for (int i = b4.length; i < twoAndThree.length; i++)
			    	threeAndFour[i] = twoAndThree[i];
			    for (int i = twoAndThree.length; i < b4.length; i++)
			    	threeAndFour[i] = b4[i];
			    int length3 = threeAndFour.length;
			    while (length3 > 0 && threeAndFour[length3 - 1] == 0)
			    	length3--;
			    if (length3 < threeAndFour.length)
			    	return Arrays.copyOf(threeAndFour, length3);
			    		
			    parBlock = threeAndFour;
		    }
		    else {
		    	parBlock = twoAndThree;
		    }
	    }
	    else {
	    	parBlock = oneAndTwo;
	    }
	    
	    return parBlock;
	}
	
	public static byte[] getMissingBlockByXOR(byte[] b1, byte[] b2, byte[] b3, byte[] b4) {
		byte[] missingBlock = null;
		
		byte[] oneAndTwo = new byte[Math.max(b1.length, b2.length)];
	    for (int i = 0; i < b1.length && i < b2.length; i++)
	        oneAndTwo[i] = (byte) (b1[i] ^ b2[i]);
	    for (int i = b2.length; i < b1.length; i++)
	        oneAndTwo[i] = b1[i];
	    for (int i = b1.length; i < b2.length; i++)
	        oneAndTwo[i] = b2[i];
	    int length = oneAndTwo.length;
	    while (length > 0 && oneAndTwo[length - 1] == 0)
	        length--;
	    if (length < oneAndTwo.length)
	        return Arrays.copyOf(oneAndTwo, length);
	    
	    if (b3 != null) {
	    	byte[] twoAndThree = new byte[Math.max(oneAndTwo.length, b3.length)];
		    for (int i = 0; i < oneAndTwo.length && i < b3.length; i++)
		    	twoAndThree[i] = (byte) (oneAndTwo[i] ^ b3[i]);
		    for (int i = b3.length; i < oneAndTwo.length; i++)
		    	twoAndThree[i] = oneAndTwo[i];
		    for (int i = oneAndTwo.length; i < b3.length; i++)
		    	twoAndThree[i] = b3[i];
		    int length2 = twoAndThree.length;
		    while (length2 > 0 && twoAndThree[length2 - 1] == 0)
		    	length2--;
		    if (length2 < twoAndThree.length)
		    	return Arrays.copyOf(twoAndThree, length2);
		    
		    if (b4 != null) {
		    	byte[] threeAndFour = new byte[Math.max(twoAndThree.length, b4.length)];
			    for (int i = 0; i < twoAndThree.length && i < b4.length; i++)
			    	threeAndFour[i] = (byte) (twoAndThree[i] ^ b4[i]);
			    for (int i = b4.length; i < twoAndThree.length; i++)
			    	threeAndFour[i] = twoAndThree[i];
			    for (int i = twoAndThree.length; i < b4.length; i++)
			    	threeAndFour[i] = b4[i];
			    int length3 = threeAndFour.length;
			    while (length3 > 0 && threeAndFour[length3 - 1] == 0)
			    	length3--;
			    if (length3 < threeAndFour.length)
			    	return Arrays.copyOf(threeAndFour, length3);
			    		
			    missingBlock = threeAndFour;
		    }
		    else {
		    	missingBlock = twoAndThree;
		    }
	    }
	    else {
	    	missingBlock = oneAndTwo;
	    }
	    
	    return missingBlock;
	}
	
	public static byte[] getXORKeyBlock(byte[] b1, byte[] b2, byte[] b3, byte[] b4, byte[] encKey) {
		byte[] oneAndTwo = new byte[Math.max(b1.length, b2.length)];
	    for (int i = 0; i < b1.length && i < b2.length; i++)
	        oneAndTwo[i] = (byte) (b1[i] ^ b2[i]);
	    for (int i = b2.length; i < b1.length; i++)
	        oneAndTwo[i] = b1[i];
	    for (int i = b1.length; i < b2.length; i++)
	        oneAndTwo[i] = b2[i];
	    int length = oneAndTwo.length;
	    while (length > 0 && oneAndTwo[length - 1] == 0)
	        length--;
	    if (length < oneAndTwo.length)
	        return Arrays.copyOf(oneAndTwo, length);
	    
	    byte[] twoAndThree = new byte[Math.max(oneAndTwo.length, b3.length)];
	    for (int i = 0; i < oneAndTwo.length && i < b3.length; i++)
	    	twoAndThree[i] = (byte) (oneAndTwo[i] ^ b3[i]);
	    for (int i = b3.length; i < oneAndTwo.length; i++)
	    	twoAndThree[i] = oneAndTwo[i];
	    for (int i = oneAndTwo.length; i < b3.length; i++)
	    	twoAndThree[i] = b3[i];
	    int length2 = twoAndThree.length;
	    while (length2 > 0 && twoAndThree[length2 - 1] == 0)
	    	length2--;
	    if (length2 < twoAndThree.length)
	    	return Arrays.copyOf(twoAndThree, length2);
	    
	    byte[] keyBlock = null;
	    if (b4 != null) {
	    	byte[] threeAndFour = new byte[Math.max(twoAndThree.length, b4.length)];
		    for (int i = 0; i < twoAndThree.length && i < b4.length; i++)
		    	threeAndFour[i] = (byte) (twoAndThree[i] ^ b4[i]);
		    for (int i = b4.length; i < twoAndThree.length; i++)
		    	threeAndFour[i] = twoAndThree[i];
		    for (int i = twoAndThree.length; i < b4.length; i++)
		    	threeAndFour[i] = b4[i];
		    int length3 = threeAndFour.length;
		    while (length3 > 0 && threeAndFour[length3 - 1] == 0)
		    	length3--;
		    if (length3 < threeAndFour.length)
		    	return Arrays.copyOf(threeAndFour, length3);
		    
		    keyBlock = new byte[Math.max(threeAndFour.length, encKey.length)];
		    for (int i = 0; i < threeAndFour.length && i < encKey.length; i++)
		    	keyBlock[i] = (byte) (threeAndFour[i] ^ encKey[i]);
		    for (int i = encKey.length; i < threeAndFour.length; i++)
		    	keyBlock[i] = threeAndFour[i];
		    for (int i = threeAndFour.length; i < encKey.length; i++)
		    	keyBlock[i] = encKey[i];
		    int length4 = keyBlock.length;
		    while (length4 > 0 && keyBlock[length4 - 1] == 0)
		    	length4--;
		    if (length4 < keyBlock.length)
		    	return Arrays.copyOf(keyBlock, length4);
	    }
	    else {
	    	keyBlock = new byte[Math.max(twoAndThree.length, encKey.length)];
	    	for (int i = 0; i < twoAndThree.length && i < encKey.length; i++)
		    	keyBlock[i] = (byte) (twoAndThree[i] ^ encKey[i]);
		    for (int i = encKey.length; i < twoAndThree.length; i++)
		    	keyBlock[i] = twoAndThree[i];
		    for (int i = twoAndThree.length; i < encKey.length; i++)
		    	keyBlock[i] = encKey[i];
		    int length4 = keyBlock.length;
		    while (length4 > 0 && keyBlock[length4 - 1] == 0)
		    	length4--;
		    if (length4 < keyBlock.length)
		    	return Arrays.copyOf(keyBlock, length4);
	    }
	    
	    return keyBlock;
	    	
	}
	
	public static byte[] getEncKeyByXOR(byte[] b1, byte[] b2, byte[] b3, byte[] b4, byte[] keyBlock) {
		byte[] oneAndTwo = new byte[Math.max(b1.length, b2.length)];
	    for (int i = 0; i < b1.length && i < b2.length; i++)
	        oneAndTwo[i] = (byte) (b1[i] ^ b2[i]);
	    for (int i = b2.length; i < b1.length; i++)
	        oneAndTwo[i] = b1[i];
	    for (int i = b1.length; i < b2.length; i++)
	        oneAndTwo[i] = b2[i];
	    int length = oneAndTwo.length;
	    while (length > 0 && oneAndTwo[length - 1] == 0)
	        length--;
	    if (length < oneAndTwo.length)
	        return Arrays.copyOf(oneAndTwo, length);
	    
	    byte[] twoAndThree = new byte[Math.max(oneAndTwo.length, b3.length)];
	    for (int i = 0; i < oneAndTwo.length && i < b3.length; i++)
	    	twoAndThree[i] = (byte) (oneAndTwo[i] ^ b3[i]);
	    for (int i = b3.length; i < oneAndTwo.length; i++)
	    	twoAndThree[i] = oneAndTwo[i];
	    for (int i = oneAndTwo.length; i < b3.length; i++)
	    	twoAndThree[i] = b3[i];
	    int length2 = twoAndThree.length;
	    while (length2 > 0 && twoAndThree[length2 - 1] == 0)
	    	length2--;
	    if (length2 < twoAndThree.length)
	    	return Arrays.copyOf(twoAndThree, length2);
	    
	    byte[] encKey = null;
	    if (b4 != null) {
	    	byte[] threeAndFour = new byte[Math.max(twoAndThree.length, b4.length)];
		    for (int i = 0; i < twoAndThree.length && i < b4.length; i++)
		    	threeAndFour[i] = (byte) (twoAndThree[i] ^ b4[i]);
		    for (int i = b4.length; i < twoAndThree.length; i++)
		    	threeAndFour[i] = twoAndThree[i];
		    for (int i = twoAndThree.length; i < b4.length; i++)
		    	threeAndFour[i] = b4[i];
		    int length3 = threeAndFour.length;
		    while (length3 > 0 && threeAndFour[length3 - 1] == 0)
		    	length3--;
		    if (length3 < threeAndFour.length)
		    	return Arrays.copyOf(threeAndFour, length3);
		    
		    encKey = new byte[Math.max(threeAndFour.length, keyBlock.length)];
		    for (int i = 0; i < threeAndFour.length && i < keyBlock.length; i++)
		    	encKey[i] = (byte) (threeAndFour[i] ^ keyBlock[i]);
		    for (int i = keyBlock.length; i < threeAndFour.length; i++)
		    	encKey[i] = threeAndFour[i];
		    for (int i = threeAndFour.length; i < keyBlock.length; i++)
		    	encKey[i] = keyBlock[i];
		    int length4 = encKey.length;
		    while (length4 > 0 && encKey[length4 - 1] == 0)
		    	length4--;
		    if (length4 < encKey.length)
		    	return Arrays.copyOf(encKey, length4);
	    }
	    else {
	    	encKey = new byte[Math.max(twoAndThree.length, keyBlock.length)];
		    for (int i = 0; i < twoAndThree.length && i < keyBlock.length; i++)
		    	encKey[i] = (byte) (twoAndThree[i] ^ keyBlock[i]);
		    for (int i = keyBlock.length; i < twoAndThree.length; i++)
		    	encKey[i] = twoAndThree[i];
		    for (int i = twoAndThree.length; i < keyBlock.length; i++)
		    	encKey[i] = keyBlock[i];
		    int length4 = encKey.length;
		    while (length4 > 0 && encKey[length4 - 1] == 0)
		    	length4--;
		    if (length4 < encKey.length)
		    	return Arrays.copyOf(encKey, length4);
	    }
	    
	    return encKey;
	}
	
	public static String generateEncKey() throws UnsupportedEncodingException {
		int strLen = 32;
		char[] chars = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ23456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new SecureRandom();
		for (int i = 0; i < strLen; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		
	}

}