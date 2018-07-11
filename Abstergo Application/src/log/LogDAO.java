package log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class LogDAO {
	private static String logFolderPath = "src/resource/logs";

	protected static ArrayList<File> getAllFile() {
		ArrayList<File> logFiles = new ArrayList<File>();
		// Get all logs on system
		File logFolder = new File(logFolderPath);
		for (File file : logFolder.listFiles()) {
			if (file.isFile()) {
				logFiles.add(file);
			}
		}
		
		return logFiles;
	}
	
	public static void writeToFile(File input, String name) {
		File output = new File(name);
		try {
			FileUtils.copyFile(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
