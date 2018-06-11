package login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LoginDAO {
	protected static ArrayList<BluetoothDevice> getPairedDevice() {
		return null;
		// TODO
	}

	protected static void addPairedDevice(ArrayList<BluetoothDevice> dbal) {
		// TODO
	}

	protected static void removePairedDevice(BluetoothDevice bd) {
		// TODO
	}

	protected static boolean getEmail(String email) {
		try {
			URL url = new URL("http://localhost/AbstergoREST/rest/Login/get/" + email);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
	
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()))); // Getting the response from the webservice

			String output = br.readLine();
			br.close();
			connection.disconnect();
			if (output != null) {
				return true;
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	protected static JsonObject getLogin(String email) {
		try {
			URL url = new URL("http://localhost/AbstergoREST/rest/Login/get/" + email);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
	
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()))); // Getting the response from the webservice

			String output;
			while ((output = br.readLine()) != null) {
				br.close();
				connection.disconnect();
				JsonElement jelement = new JsonParser().parse(output);
				return jelement.getAsJsonObject();
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String createLogin(LoginPageModel loginModel, BluetoothDevice device) {
		try {
			// Open Connection to REST Server
			URL url = new URL("http://localhost/AbstergoREST/rest/Login");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			
			// Convert Object to Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String jsonInString = mapper.writeValueAsString(loginModel);
			String jsonInString2 = mapper.writeValueAsString(device);
			String completeJson = jsonInString.substring(0, jsonInString.length() - 1) + "," + jsonInString2.substring(1);
			
			// Pass in the Json Object to REST Server
			OutputStream os = connection.getOutputStream();
			os.write(completeJson.getBytes());
			os.flush();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ connection.getResponseCode());
			}

			// Get Response from the REST Server
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream()))); 

			// Return Value
			String output;
			while ((output = br.readLine()) != null) {
				br.close();
				connection.disconnect();
				return output;
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
