package log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;

import logExtra.KeyPair;

public class LogDAO {
	private static String logFolderPath = "src/resource/logs";

	protected static ArrayList<File> getAllFile() {
		ArrayList<File> logFiles = new ArrayList<File>();
		// Get all logs on system
		File logFolder = new File(logFolderPath);
		File[] fileList = logFolder.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (file.isFile()) {
					logFiles.add(file);
				}
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

	public static PrivateKey getPrivateKey(String user) {
		BufferedReader br = null;
		try {
			URL url = new URL("https://localhost/AbstergoREST/rest/KeyPair/private/" + user);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "text/plain");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			String output;
			while ((output = br.readLine()) != null) {
				br.close();
				input.close();
				inputStream.close();
				connection.disconnect();

				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(output));
				KeyFactory factory = KeyFactory.getInstance("RSA");
				return factory.generatePrivate(spec);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
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

	public static PublicKey getPublicKey(String user) {
		BufferedReader br = null;
		try {
			URL url = new URL("https://localhost/AbstergoREST/rest/KeyPair/public/" + user);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "text/plain");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			String output;
			while ((output = br.readLine()) != null) {
				br.close();
				input.close();
				inputStream.close();
				connection.disconnect();

				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(output));
				KeyFactory factory = KeyFactory.getInstance("RSA");
				return factory.generatePublic(spec);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
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

	public static String getEncryptionKey(String username) {
		BufferedReader br = null;
		try {
			URL url = new URL("https://localhost/AbstergoREST/rest/KeyPair/encryption/" + username);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "text/plain");

			InputStream inputStream = connection.getInputStream();
			InputStreamReader input = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			br = new BufferedReader(input); // Getting the response from the webservice

			String output;
			while ((output = br.readLine()) != null) {
				br.close();
				input.close();
				inputStream.close();
				connection.disconnect();

				return output;
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

	public static String createKeys(String username) {
		BufferedReader br = null;
		try {
			// Open Connection to REST Server
			URL url = new URL("https://localhost/AbstergoREST/rest/KeyPair");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier((hostname, session) -> true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// New KeyPair Model
			KeyPair keyPair = new KeyPair(username);
			
			// Convert Object to Json
			ObjectMapper mapper = new ObjectMapper();
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String jsonInString = mapper.writeValueAsString(keyPair);

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
}
