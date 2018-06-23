package log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Setting {
	private Preferences preferences;
	// I will add more settings when I remember
	private String ID1 = "LogFile";			// Which Log file to "store" E.g. "Application", "System" & "Abstergo"
	private String ID2 = "LogDisplay"; 		// What to ~display~ (don't confuse with storing, storing everything) in Logs E.g. Event Source ID, Event Time Written, Event Data
	private String ID3 = "IncomingAccept"; 	// Whether to enable receiving files
	private String ID4 = "IncomingMaximum";	// Maximum file size to accept
	
	public Setting() {
		preferences = Preferences.userNodeForPackage(getClass());
	}
	
	public void getPreference() {
		// Getting values and setting defaults
		// TODO Remove printing
		// Maybe create object
		System.out.println(preferences.get(ID1, "Application;Abstergo"));
		System.out.println(preferences.get(ID2, "Event Source;Event Type;Event ID;Event Time Generated"));
		System.out.println(preferences.getBoolean(ID3, true));
		System.out.println(preferences.getLong(ID4, 50000000)); //50 MB?
	}
	
	public void setPreference() { // Create new object?
		OutputStream outputStream = null;
		try {
			// Setting values
			// TODO
			preferences.put(ID1, "Application;Abstergo");
			preferences.put(ID2, "Event Source");
			preferences.putBoolean(ID3, true);
			preferences.putLong(ID4, 20000000);
			
			outputStream  = new FileOutputStream("src/resource/settings/hello.xml");
			preferences.exportSubtree(outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void restoreDefaults() {
		// Deleting preferences to restore defaults
		preferences.remove(ID1);
		preferences.remove(ID2);
		preferences.remove(ID3);
		preferences.remove(ID4);
	}

	public static void main(String[] args) {
		Setting test = new Setting();
		test.getPreference();
	}
}
