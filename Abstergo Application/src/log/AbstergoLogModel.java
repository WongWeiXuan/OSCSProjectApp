package log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import logExtra.AESEncryption;

public class AbstergoLogModel {
	private String fileName;
	private byte[] key;

	public AbstergoLogModel(String key) {
		this.key = Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
	}

	// decrypt
	private File decryptFile() {
		// !!Never set filename!!
		File file = new File("resource/logs/" + fileName);
		AESEncryption aes = new AESEncryption(key);
		return aes.decryptFile(file);
	}

	// encrypt
	private File encryptFile(File file) {
		AESEncryption aes = new AESEncryption(key);
		return aes.encryptFile(file);
	}

	// read
	// TODO Process log to look nicer
	public ArrayList<ArrayList<String>> read() {
		try {
			ArrayList<ArrayList<String>> returnArray = new ArrayList<ArrayList<String>>();

			File file = decryptFile();
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader fr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				ArrayList<String> stringArray = new ArrayList<String>();
				InputStream input = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
				Scanner sc = new Scanner(input, "UTF-8");
				sc.useDelimiter(";");
				while (sc.hasNext()) {
					stringArray.add(sc.next());
				}
				returnArray.add(stringArray);
				sc.close();
				input.close();
			}

			br.close();
			fr.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// write
	private File writeToFile(ArrayList<ArrayList<String>> stringArray) {
		try {
			File temp = File.createTempFile(fileName, ".txt");
			FileOutputStream fos = new FileOutputStream(temp, true);
			Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(writer);
			PrintWriter out = new PrintWriter(bw);
			for (ArrayList<String> array : stringArray) {
				boolean first = true;

				for (String s : array) {
					if (first) {
						out.append(s);
						first = false;
					} else {
						out.append(";" + s);
					}
				}
				out.append("\n");
			}
			out.flush();
			out.close();
			bw.close();
			writer.close();
			fos.close();

			return temp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
