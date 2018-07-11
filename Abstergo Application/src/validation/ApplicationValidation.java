package validation;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ApplicationValidation implements Runnable{

	@Override
	public void run() {
		Map<String, String> fileHashMap = ApplicationValidationDAO.getFileHashMap();

		System.out.println("Files that failed: ");
		ArrayList<String> array = compareHashesPlus(fileHashMap);
		for(String s : array) {
			System.out.println("\t" + s);
		}
		
		if(array.size() > 0) {
			ApplicationValidationDAO.getFile("DiscoveryThread.java");
		}
	}
	
	private boolean compareHashes(Map<String, String> fileHashMap) {
		if(compareHashesPlus(fileHashMap).size() > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	private ArrayList<String> compareHashesPlus(Map<String, String> fileHashMap) {
		ArrayList<String> stringArray = new ArrayList<String>();
		JsonArray jsonArray = ApplicationValidationDAO.getFileHashes();
		for(JsonElement je : jsonArray) {
			JsonObject object = je.getAsJsonObject();
			String compareName = object.get("fileName").getAsString();
			String compareSHA1 = object.get("fileSHA1").getAsString();
			String dbSHA1 = fileHashMap.get(compareName);
			if(!dbSHA1.equals(compareSHA1)) {
				stringArray.add(compareName);
			}
		}
		
		return stringArray;
	}
	
	public static void main(String[] arg0) {
		Thread thread = new Thread(new ApplicationValidation());
		thread.start();
	}
}
