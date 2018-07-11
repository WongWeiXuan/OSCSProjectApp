package validation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
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
			URL url = new URL("http://localhost/AbstergoREST/rest/FileHash/get/" + getFileMapping(fileName));
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
			String line = getFileMapping(name).replace("/", "~");
			URL url = new URL("http://localhost/AbstergoREST/rest/Download/" + line);
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
			
			deleteLastLine(file);
			File toBeReplaced = new File(getFileMapping(name));

			// Copy text to original file
			copyTextOver(file, toBeReplaced);
			file.delete();
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

	private static void copyTextOver(File file, File toBeReplaced) {
		BufferedWriter writer = null;
		try {
			List<String> stringList = Files.readLines(file, Charset.forName("UTF-8"));
			writer = new BufferedWriter(new FileWriter(toBeReplaced));

			for (String s : stringList) {
				writer.write(s);
				writer.newLine();
			}

			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	protected static Map<String, String> getFileHashMap() {
		try {
			check(new File("src"));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("SIZE: " + fileHashArray.size());
		return fileHashMap;
	}

	private static void check(File file) throws IOException {
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
					writeToFile(descendants.getName(), descendants.getPath());
					FileHash fileHash = new FileHash(descendants.getName(), Transcation.generateSHA(descendants));
//					System.out.println(fileHash.getFileName() + ", \n\t" + fileHash.getFileSHA1());
					fileHashMap.put(fileHash.getFileName(), fileHash.getFileSHA1());
				}
			}
		}
	}

	private static void deleteLastLine(File file) {
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			long length = file.length() - 1;
			byte b;
			do {
				length -= 1;
				raf.seek(length);
				b = raf.readByte();
			} while (b != 10);
			raf.setLength(length + 1);
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFileMapping(String fileName) {
		try {
			Map<String, String> fileMap = new HashMap<String, String>();

			Scanner sc = new Scanner(new File("src/validation/FileMapping.txt"));

			while (sc.hasNextLine()) {
				Scanner sc2 = new Scanner(sc.nextLine());
				sc2.useDelimiter(";");
				fileMap.put(sc2.next(), sc2.next());
				sc2.close();
			}
			sc.close();

			return fileMap.get(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void clearFileMap() {
		PrintWriter writer;
		try {
			// Clearing file
			writer = new PrintWriter(new File("src/validation/FileMapping.txt"), "UTF-8");
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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

	// For development
	private static void writeToFile(String name, String path) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("src/validation/FileMapping.txt", true));
			bw.write(name + ";" + path.replace("\\", "/") + "\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] arg0) {
		ApplicationValidationDAO.clearFileMap();
		updateFileHashes(ApplicationValidationDAO.getFileHashMap());
	}
}
