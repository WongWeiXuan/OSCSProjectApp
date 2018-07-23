package login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bluetooth.BluetoothDevice;

public class LoginDAO {
	protected static JsonObject getPairedDevice(String email) {
		BufferedReader br = null;
		try {
			URL url = new URL("https://localhost/AbstergoREST/rest/Login/getDevice/" + email);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			String output = br.readLine();
			br.close();
			input.close();
			inputStream.close();
			connection.disconnect();
			if (output != null) {
				br.close();
				connection.disconnect();
				JsonElement jelement = new JsonParser().parse(output);
				return jelement.getAsJsonObject();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void addPairedDevice(ArrayList<BluetoothDevice> dbal) {
		// TODO
	}

	public static void removePairedDevice(BluetoothDevice bd) {
		// TODO
	}

	protected static boolean getEmail(String email) {
		BufferedReader br = null;
		try {
			URL url = new URL("https://localhost/AbstergoREST/rest/Login/get/" + email);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			String output = br.readLine();
			br.close();
			input.close();
			inputStream.close();
			connection.disconnect();
			if (output != null) {
				return true;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	protected static JsonObject getLogin(String email) {
		BufferedReader br = null;
		try {
			URL url = new URL("https://localhost/AbstergoREST/rest/Login/get/" + email);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			String output;
			while ((output = br.readLine()) != null) {
				br.close();
				input.close();
				inputStream.close();
				connection.disconnect();
				break;
			}
			JsonElement jelement = new JsonParser().parse(output);
			return jelement.getAsJsonObject();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String createLogin(LoginPageModel loginModel, BluetoothDevice device) {
		BufferedReader br = null;
		try {
			// Open Connection to REST Server
			URL url = new URL("https://localhost/AbstergoREST/rest/Login");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// Convert Object to Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String jsonInString = mapper.writeValueAsString(loginModel);
			String jsonInString2 = mapper.writeValueAsString(device);
			String completeJson = jsonInString.substring(0, jsonInString.length() - 1) + ","
					+ jsonInString2.substring(1);

			// Pass in the Json Object to REST Server
			OutputStream os = connection.getOutputStream();
			os.write(completeJson.getBytes(StandardCharsets.UTF_8));
			os.flush();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			}

			// Get Response from the REST Server
			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input);

			// Return Value
			String output;
			while ((output = br.readLine()) != null) {
				continue;
			}
			br.close();
			input.close();
			inputStream.close();
			connection.disconnect();
			return output;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
