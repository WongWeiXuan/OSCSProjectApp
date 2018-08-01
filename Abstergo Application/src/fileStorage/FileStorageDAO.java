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
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileStorageDAO {
	
	public static String uploadFile(FileStorage fs) {
		BufferedReader br = null;
		try {
			// Open Connection to REST Server
			URL url = new URL("http://abstergorest.appspot.com/rest/FileStorage");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// Convert Object to Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String jsonInString = mapper.writeValueAsString(fs);

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
	
	public static void main(String[] args) {
		FileStorage fs = new FileStorage();
		fs.setFileName("Hello World");
		fs.setFileType("TXT");
		fs.setDateCreated("1/8/2018");
		fs.setFile(new File("D:\\Hello World.txt"));
		System.out.println(fs.getFileName());
	}
}
