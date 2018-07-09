package validation;

import java.io.File;
import java.io.FilenameFilter;

import logExtra.Transcation;
// TODO Obfuscate this class
public class ApplicationValidation implements Runnable{

	@Override
	public void run() {
		File folder = new File("src");
		check(folder);
	}
	
	private void check(File file) {
		if(file != null) {
			for(File descendants: file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if(name.equals("resource")) {
						return false;
					}	
					else {
						return true;
					}
				}
				
			})) {
				if(descendants.isDirectory()) {
					check(descendants);
				} else if(descendants.isFile()){
					// TODO Check with database hash
					// If correct do nothing
					// Else prompt error
					System.out.println(descendants.getName() + ", \n\t" + Transcation.generateSHA(descendants));
				}
			}
		}
	}
	
	/*
	public static void main(String[] arg0) {
		Thread thread = new Thread(new ApplicationValidation());
		thread.start();
	}
	*/
}
