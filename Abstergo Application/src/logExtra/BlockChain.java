package logExtra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockChain {
	public static ArrayList<Block> blockArray = new ArrayList<Block>();
	public static int difficulty = 2;
	
	public BlockChain() {
		try {
			// Retrieve block chain
			// TODO
			File file = new File("src/resource/settings/test.txt");
			Transcation trans = new Transcation("User1", "User2", file);
			Block block = new Block(trans, "");
			block.mineBlock(difficulty);
			
			blockArray.add(block);
			// FileStream
			FileOutputStream fos = new FileOutputStream(file, true);
			Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(writer);
			bw.append("\n Hello");
			bw.flush();
			
			Transcation trans2 = new Transcation("User1", "User2", file);
			Block block2 = new Block(trans2, block);
			block2.mineBlock(difficulty);
			
			blockArray.add(block2);
			
			bw.append("\n bye");
			bw.flush();
			
			Transcation trans3 = new Transcation("User1", "User2", file);
			Block block3 = new Block(trans3, block2);
			block3.mineBlock(difficulty);
			
			blockArray.add(block3);
			bw.close();
			writer.close();
			fos.close();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
					System.out.println("Current Hashes not equal");			
					return false;
				}
			
				//compare previous hash and registered previous hash
				if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
					System.out.println("Previous Hashes not equal");
					return false;
				}
				//check if hash is solved
				if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
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
			Block block = new Block(trans, getLastHash());
			block.mineBlock(difficulty);
			blockArray.add(block);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String getLastHash() {
		return blockArray.get(blockArray.size()-1).getFileHash();
	}
	
	public static void main(String[] arg0) {
		File file = new File("src/resource/settings/test.txt");
		BlockChain bc = new BlockChain();
		System.out.println("Valid: " + bc.isChainValid());
		
		Scanner sc = new Scanner(System.in);
		sc.next();
		
		if(Transcation.generateSHA(file).equals(bc.getLastHash())) {
			System.out.println("Verified.");
		} else {
			System.out.println("UnVerified?");
		}
		sc.close();
	}
}
