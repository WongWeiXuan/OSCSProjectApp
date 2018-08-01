package email.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class test {
	public static void main(String [] args) {
		File filename2 = new File("src/util/Directory.txt");
		String path = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filename2))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				path = sCurrentLine;
			}

		} catch (IOException ee) {
			ee.printStackTrace();
		}
		path = path.replace(";","");
		System.out.println(path);
	}
}
