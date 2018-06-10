package login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

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
	
	protected static String createLogin(LoginPageModel loginModel, BluetoothDevice device) {
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
				return output;
			}
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		LoginPageModel loginModel = new LoginPageModel("Wolf2", "7C68100A42966494D98389C85C33F177963CD39CFBE9AF5C6C618DD3DB88775F5AA20E4CAB2ECE36FBC2E2DE5A2D82EB0E58CC60B136A1F5345090E551DCBA781B99E27E522E79C9966CAD310E7A1C4E8CB66C20BDFCFDADBA2758A57F15C2528C39D0A402CFBAB4E7D26C84CCB01D81936976D03D9EC827F37053B1D332D54D"
				,"FBD5CE8218259C3DCCDF537963B74A3C49358D3033CE28776D0AF3AD0EECD71507A8730F799D00B1648B8266F04C0B6DBDEC92E4BD389C52316C359B0B6120758043AC9AE60C91D50EE8626BFDDD76B29D40CA83E75E8784B88D95B0FE166923842693A8B8FC8909C8344FA0381E7B0802D8FA89E558CA582B2B1EFBBCF45B06");
		BluetoothDevice device = new BluetoothDevice("Weixuan", "B853AC42D651", "512");
		System.out.println(createLogin(loginModel, device));
	}
}
