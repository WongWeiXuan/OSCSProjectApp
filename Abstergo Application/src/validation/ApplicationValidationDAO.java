package validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import logExtra.Transcation;

public class ApplicationValidationDAO {
	private static Map<String, String> fileHashMap = new HashMap<String, String>();

	protected static JsonArray getFileHashes() {
		BufferedReader br = null;
		try {
			URL url = new URL("http://localhost/AbstergoREST/rest/FileHash/get");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
				
				JsonElement jelement = new JsonParser().parse(output);
				return jelement.getAsJsonArray();
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
	
	protected static JsonObject getFileHashFromFileName(String fileName) {
		BufferedReader br = null;
		try {
			URL url = new URL("http://localhost/AbstergoREST/rest/FileHash/get/" + fileName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
	
	protected static void getFile(String name) {
		BufferedReader br = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			URL url = new URL("http://localhost/AbstergoREST/rest/Download/" + name);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");

			File file = File.createTempFile(name, ".java");
			inputStream = connection.getInputStream();
			outputStream = new FileOutputStream(file);
		
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			
			JsonObject json = getFileHashFromFileName(name);
			System.out.println("DOWNLOAD SHA1: " + Transcation.generateSHA(file) + "; " + json.get("fileSHA1").getAsString());
			if (json.get("fileSHA1").getAsString().equals(Transcation.generateSHA(file))) {
				System.out.println("HMMMMM...");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (outputStream != null)
					outputStream.close();
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Development method
	public static String updateFileHashes(Map<String, String> fileHashMap) {
		BufferedReader br = null;
		try {
			// Open Connection to REST Server
			URL url = new URL("http://localhost/AbstergoREST/rest/FileHash");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// Convert Object to Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String jsonInString = mapper.writeValueAsString(fileHashMap);

			// Pass in the Json Object to REST Server
			OutputStream os = connection.getOutputStream();
			os.write(jsonInString.getBytes(StandardCharsets.UTF_8));
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

	protected static Map<String, String> getFileHashMap() {
		check(new File("src"));
//		System.out.println("SIZE: " + fileHashArray.size());
		return fileHashMap;
	}

	private static void check(File file) {
		if (file != null) {
			for (File descendants : file.listFiles(new FilenameFilter() { // Hashing everything except resource folder

				@Override
				public boolean accept(File dir, String name) {
					if (name.equals("resource")) {
						return false;
					} else {
						return true;
					}
				}

			})) {
				if (descendants.isDirectory()) {
					check(descendants);
				} else if (descendants.isFile()) {
					FileHash fileHash = new FileHash(descendants.getName(), Transcation.generateSHA(descendants));
//					System.out.println(fileHash.getFileName() + ", \n\t" + fileHash.getFileSHA1());
					fileHashMap.put(fileHash.getFileName(), fileHash.getFileSHA1());
				}
			}
		}
	}

	public static void main(String[] arg0) {
		updateFileHashes(ApplicationValidationDAO.getFileHashMap());
	}
}
