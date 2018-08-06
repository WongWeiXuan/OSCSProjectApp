package email.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import email.controller.EmailDAO;

public class test {
	public static void main(String [] args) throws JsonProcessingException {
		Email e2 = new Email("abstergoapp@gmail.com", "abstergoapp@gmail.com", "@bsterg0@pp");
		//EmailDAO.createEmail(e2);
		
		
		JsonArray json = EmailDAO.getdetails("abstergoapp@gmail.com");
		for(JsonElement e : json) {
			JsonObject object = e.getAsJsonObject();
			System.out.println(object.get("address").getAsString());
			System.out.println(object.get("password").getAsString());
		}
	}
}
