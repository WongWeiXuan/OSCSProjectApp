package email.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import email.controller.EmailDAO;

public class test {
	public static void main(String [] args) throws JsonProcessingException {
		//Email e2 = new Email("abstergoapp@gmail.com", "test22@gmail.com", "@bsterg0@pp");
		//EmailDAO.createEmail(e2);
		
		ArrayList<customEmail> em = new ArrayList<customEmail>();
		JsonArray json = EmailDAO.getdetails("abstergoapp@gmail.com");
		for(JsonElement e : json) {
			JsonObject object = e.getAsJsonObject();
			customEmail ce = new customEmail(object.get("address").getAsString(), object.get("password").getAsString());
			em.add(ce);
		}
		System.out.println(em.size());
		for(int i=0; i<em.size();i++) {
			System.out.println(em.get(i).getEmail1());
		}
	}
}
