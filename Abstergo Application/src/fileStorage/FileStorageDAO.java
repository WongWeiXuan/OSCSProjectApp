package fileStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FileStorageDAO {
	
	public static JsonObject getUserFiles(String username, String fileName) {
		BufferedReader br = null;
		try {
			URL url = new URL("http://abstergorest.appspot.com/rest/FileStorage/getuserfiles" + username + fileName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
	
	public static String uploadFile(FileStorage fs) {
		BufferedReader br = null;
		try {
			// Open Connection to REST Server
			URL url = new URL("http://abstergorest.appspot.com/rest/FileStorage/uploadfile");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// Convert Object to Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String jsonInString = mapper.writeValueAsString(fs);
			
			System.out.println(jsonInString);
			
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
	
	public static void downloadFile(String username, String fileName, File into) {
		BufferedReader br = null;
		try {
			URL url = new URL("http://abstergorest.appspot.com/rest/FileStorage/downloadfile" + username + fileName + into);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			File fileToDownload = into;
			FileUtils.copyInputStreamToFile(inputStream, fileToDownload);
			
			String output = br.readLine();
			br.close();
			input.close();
			inputStream.close();
			connection.disconnect();
			if (output != null) {
				br.close();
				connection.disconnect();
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
	}
	
	public static JsonObject getSplitFiles(String username, String fileName) {
		BufferedReader br = null;
		try {
			URL url = new URL("http://abstergorest.appspot.com/rest/FileStorage/getsplitfiles" + username + fileName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
	
	public static void main(String[] args) throws Exception {
		FileStorage fs = new FileStorage();
		fs.setUsername("test@example.com");
		FileSplit.splitFile(new File("D:\\Hello World.txt"), fs);
		
		System.out.println(uploadFile(fs));
	}
}
