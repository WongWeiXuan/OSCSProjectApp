package logExtra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import log.LogDetailsModel;

public class BlockChainDAO {
	public static ArrayList<Block> readBlocks(File file) {
		try {
			ArrayList<Block> blockArray = new ArrayList<Block>();
			Scanner sc = new Scanner(file);
			while(sc.hasNextLine()) {
				Scanner sc1 = new Scanner(sc.nextLine());
				sc1.useDelimiter("String ");
				String hash 				= sc1.next();
				String previousHash			= sc1.next();
				String userFrom				= sc1.next();
				String userTo				= sc1.next();
				String time					= sc1.next();
				String fileHash				= sc1.next();
				boolean broadcastFileHash	= Boolean.parseBoolean(sc1.next());
				sc1.close();
				
				Transcation trans = new Transcation(userFrom, userTo, Long.parseLong(time), fileHash, broadcastFileHash);
				blockArray.add(new Block(trans, hash, previousHash));
			}
			sc.close();
			
			return blockArray;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeToBlockChain(Block block, String logName, boolean remote) {
		try {
			File file; 
			if(remote) {
				file = new File("src/resource/chain/" + logName + "ChainRemote.txt");
			} else {
				file = new File("src/resource/chain/" + logName + "Chain.txt");
			}
			FileOutputStream fos = new FileOutputStream(file, true);
			Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(writer);
			
			PrintWriter out = new PrintWriter(bw);
			out.append(block.getHash());
			out.append("~" + block.getPreviousHash());
			out.append("~" + block.getTranscation().getUserFrom());
			out.append("~" + block.getTranscation().getUserTo());
			out.append("~" + block.getTranscation().getTime());
			out.append("~" + block.getTranscation().getFileHash());
			out.append("~" + String.valueOf(block.getTranscation().isBroadcastFile()));
			out.append("\n");
			out.flush();
			out.close();
			
			bw.close();
			writer.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
