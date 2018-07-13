package validation;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javafx.concurrent.Task;

public class ApplicationValidation extends Task<Void> {
	@Override
	protected Void call() throws Exception {
		return null;
	}

	protected ArrayList<String> compareHashesPlus(Map<String, String> fileHashMap) {
		ArrayList<String> stringArray = new ArrayList<String>();
		JsonArray jsonArray = ApplicationValidationDAO.getFileHashes();
		for (JsonElement je : jsonArray) {
			JsonObject object = je.getAsJsonObject();
			String compareName = object.get("fileName").getAsString();
			String compareSHA1 = object.get("fileSHA1").getAsString();
			String dbSHA1 = fileHashMap.get(compareName);
			if (!dbSHA1.equals(compareSHA1)) {
				stringArray.add(compareName);
			}
		}

		return stringArray;
	}
}