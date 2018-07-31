package logExtra;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockChain {
	private ArrayList<Block> blockArray = new ArrayList<Block>();
	private int difficulty = 2;
	private String logName;
	
	public BlockChain(String logName, String address, boolean remote) {
		this.logName = logName;
		if(remote) {
			getRemoteChain(address);
		} else {
			getChain();
		}
	}
	
	private void getChain() {
		// Retrieve block chain
		this.blockArray = BlockChainDAO.readBlocks(new File("src/resource/chain/" + logName + "Chain.txt"), "");
	}
	
	private void getRemoteChain(String address) {
		// Retrieve block chain
		this.blockArray = BlockChainDAO.readBlocks(new File("src/resource/chain/" + logName + "ChainRemote.txt"), address);
	}
	
	public Boolean isChainValid() {
		try {
			Block currentBlock;
			Block previousBlock;
			
			String hashTarget = new String(new char[difficulty]).replace('\0', '0');
			
			//loop through blockchain to check hashes:
			for(int i=1; i < blockArray.size(); i++) {
				currentBlock = blockArray.get(i);
				previousBlock = blockArray.get(i-1);
				//compare registered hash and calculated hash:
				if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
					System.out.println("Current Hashes not equal");			
					return false;
				}
			
				//compare previous hash and registered previous hash
				if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
					System.out.println("Previous Hashes not equal");
					return false;
				}
				//check if hash is solved
				if(!currentBlock.getHash().substring( 0, difficulty).equals(hashTarget)) {
					System.out.println("This block hasn't been mined");
					return false;
				}
			}
			return true;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void addBlock(Transcation trans) {
		try {
			boolean remote = false;
			if(trans.isBroadcastFile()) {
				remote = true;
			}
			String lastHash = getLastHash();
			if(lastHash == null) {
				lastHash = "";
			}
			Block block = new Block(trans, lastHash);
			block.mineBlock(difficulty);
			blockArray.add(block);
			
			BlockChainDAO.writeToBlockChain(block, logName, remote);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String getLastHash() {
		if(blockArray.size() > 0) {
			return blockArray.get(blockArray.size()-1).getHash();
		}
		return null;
	}
	
	public String getLastFileHash() {
		if(blockArray.size() > 0) {
			return blockArray.get(blockArray.size()-1).getFileHash();
		}
		return null;
	}
	
	public ArrayList<String> getListOfNetwork() {
		ArrayList<String> arrayList = new ArrayList<String>();
		for(Block b : blockArray) {
			String addressTo = b.getTranscation().getUserTo();
			if(b.getTranscation().isDeleted() || (!arrayList.contains(addressTo) && !"0.0.0.0".equals(addressTo))) {
				if(b.getTranscation().isDeleted()) {
					arrayList.remove(addressTo);
				} else {
					arrayList.add(addressTo);
				}
			}
			
		}
		
		return arrayList;
	}
	
	public static void main(String[] arg0) {
		BlockChain bc = new BlockChain("Application", "", false);
		System.out.println("Valid: " + bc.isChainValid());
		
		Scanner sc = new Scanner(System.in);
		sc.next();
		
//		if(Transcation.generateSHA(file).equals(bc.getLastHash())) {
//			System.out.println("Verified.");
//		} else {
//			System.out.println("UnVerified?");
//		}
		sc.close();
	}
}
