package logExtra;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Block {
	private String hash;
	private String previousHash;
	private Transcation transcation;
	private int nonce;

	public Block(Transcation transcation, String previousHash) throws NoSuchAlgorithmException {
		this.transcation = transcation;
		this.previousHash = previousHash;
	}
	
	public Block(Transcation transcation, String hash, String previousHash, int nonce) throws NoSuchAlgorithmException {
		this.transcation = transcation;
		this.hash = hash;
		this.previousHash = previousHash;
		this.nonce = nonce;
	}
	
	public Block(Transcation transcation, Block previousBlock) throws NoSuchAlgorithmException {
		this.transcation = transcation;
		this.previousHash = previousBlock.getHash();
	}

	public Block(String userFrom, String userTo, File file, String previousHash) throws NoSuchAlgorithmException {
		this.transcation = new Transcation(userFrom, userTo, file);
		this.previousHash = previousHash;
		this.hash = calculateHash();
	}

	public String calculateHash() throws NoSuchAlgorithmException {
		String calculatedHash = StringUtil
				.applySha256(previousHash + transcation.getUserFrom() + transcation.getUserTo()
						+ Long.toString(transcation.getTime()) + Integer.toString(nonce) + transcation.getFileHash() + transcation.isBroadcastFile() + transcation.isDeleted());

		return calculatedHash;
	}

	public void mineBlock(int difficulty) throws NoSuchAlgorithmException {
		String target = new String(new char[difficulty]).replace('\0', '0');
		this.hash = calculateHash();
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Mined block hash: " + hash);
	}
	
	public String getFileHash() {
		if(transcation.getFileHash().equals(".")) {
			return "";
		}
		return transcation.getFileHash();
	}

	protected String getHash() {
		return hash;
	}

	protected String getPreviousHash() {
		if(previousHash.isEmpty()) {
			return ".";
		}
		return previousHash;
	}
	
	protected int getNonce() {
		return nonce;
	}

	protected Transcation getTranscation() {
		return transcation;
	}
}
