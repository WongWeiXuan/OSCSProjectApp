package log;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Block {
	public String hash;
	public String previousHash;
	private Transcation transcation;
	private int nonce;

	public Block(Transcation transcation, String previousHash) throws NoSuchAlgorithmException {
		this.transcation = transcation;
		this.previousHash = previousHash;
		this.hash = calculateHash();
	}

	public Block(String userFrom, String userTo, File file, String previousHash) throws NoSuchAlgorithmException {
		this.transcation = new Transcation(userFrom, userTo, file);
		this.previousHash = previousHash;
		this.hash = calculateHash();
	}

	public String calculateHash() throws NoSuchAlgorithmException {
		String calculatedHash = StringUtil
				.applySha256(previousHash + transcation.getUserFrom() + transcation.getUserTo()
						+ Long.toString(transcation.getTime()) + Integer.toString(nonce) + transcation.getFileHash());

		return calculatedHash;
	}

	public void mineBlock(int difficulty) throws NoSuchAlgorithmException {
		String target = new String(new char[difficulty]).replace('\0', '0');
		while (!hash.substring(0, difficulty).equals(target)) {
			Random rd = new Random();
			nonce = rd.nextInt();
			hash = calculateHash();
		}
		System.out.println("Block Mined!!!: " + hash);
	}
}
