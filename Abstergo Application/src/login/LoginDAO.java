package login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginDAO {
	final private static String TEXTFILE = "src/login/BaconAndEggs.txt";

	protected static ArrayList<BluetoothDevice> getPairedDevice() throws FileNotFoundException {
		ArrayList<BluetoothDevice> bda = new ArrayList<BluetoothDevice>();
		File file = new File(TEXTFILE);
		Scanner sc = new Scanner(file);
		while (sc.hasNextLine()) {
			Scanner sc1 = new Scanner(sc.nextLine());
			sc1.useDelimiter(";");
			BluetoothDevice bd = new BluetoothDevice(sc1.next(), sc1.next(), sc1.nextInt());
			bda.add(bd);
			sc1.close();
		}
		sc.close();

		return bda;
	}

	protected static void addPairedDevice(ArrayList<BluetoothDevice> dbal) throws IOException {
		boolean first = true;
		File file = new File(TEXTFILE);
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (BluetoothDevice bd : dbal) {
			if (first) {
				writer.write(bd.getBluetoothAddress() + ";" + bd.getFriendlyName() + ";" + bd.getMajorClass());
				first = false;
			} else {
				writer.write("\n" + bd.getBluetoothAddress() + ";" + bd.getFriendlyName()+ ";" + bd.getMajorClass());
			}
		}
		writer.close();
	}

	protected static void removePairedDevice(BluetoothDevice bd) throws IOException {
		File file = new File(TEXTFILE);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		String lineToRemove = bd.getBluetoothAddress() + ";" + bd.getFriendlyName()+ ";" + bd.getMajorClass();
		String currentLine;

		while ((currentLine = reader.readLine()) != null) {
			// trim newline when comparing with lineToRemove
			String trimmedLine = currentLine.trim();
			if (trimmedLine.equals(lineToRemove))
				continue;
			writer.write(currentLine + System.getProperty("line.separator"));
		}
		writer.close();
		reader.close();
	}
}
