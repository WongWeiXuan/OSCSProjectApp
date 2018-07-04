package setting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Setting {
	private Preferences preferences;
	// I will add more settings when I remember
	private String ID1 = "LogFile"; // Which Log file to "store" E.g. "Application", "System" & "Abstergo"
	private String ID2 = "LogDisplay"; // What to ~display~ (don't confuse with storing, storing everything) in Logs
										// E.g. Event Source ID, Event Time Written, Event Data
	private String ID3 = "IncomingAccept"; // Whether to enable receiving files
	private String ID4 = "IncomingMaximum"; // Maximum file size to accept
	private String ID5 = "OnDisconnect"; // When device's bluetooth lose connection
	private String ID6 = "Timeout";

	public Setting() {
		preferences = Preferences.userNodeForPackage(getClass());
	}

	public SettingModel getPreference() {
		// Getting values and setting defaults
		SettingModel model = new SettingModel();
		model.setLogFile(preferences.get(ID1, "Application;Abstergo"));
		model.setLogDisplay(preferences.get(ID2, "Event Source;Event Type;Event ID;Event Time Generated"));
		model.setIncomingAccept(preferences.getBoolean(ID3, true));
		model.setIncomingMaximum(preferences.getLong(ID4, 50));
		model.setOnDiconnection(preferences.get(ID5, "Logout"));
		model.setTimeout(preferences.getInt(ID6, 15));

		return model;
	}

	public void setPreference(SettingModel model) {
		OutputStream outputStream = null;
		try {
			// Setting values
			preferences.put(ID1, model.getLogFileString());
			preferences.put(ID2, model.getLogDisplayString());
			preferences.putBoolean(ID3, model.isIncomingAccept());
			preferences.putLong(ID4, model.getIncomingMaximum());
			preferences.put(ID5, model.getOnDiconnection());

			// Write to file
			outputStream = new FileOutputStream("src/resource/settings/hello.xml");
			preferences.exportSubtree(outputStream);

			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void restoreDefaults() {
		OutputStream outputStream = null;
		try {
			// Deleting preferences to restore defaults
			preferences.remove(ID1);
			preferences.remove(ID2);
			preferences.remove(ID3);
			preferences.remove(ID4);
			preferences.remove(ID5);

			// Write to file
			outputStream = new FileOutputStream("src/resource/settings/hello.xml");
			preferences.exportSubtree(outputStream);

			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Setting test = new Setting();
		SettingModel model = test.getPreference();
		// test.restoreDefaults();
		System.out.println(model.toString());
	}
}
