package email.controller.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import email.controller.ModelAccess;
import email.controller.services.CreateAndRegisterEmailAccountService;
import email.model.customEmail;

public class PersistenceAcess {
	
	private List<ValidAccount> persistedList = null;
	public PersistenceAcess(ModelAccess modelAccess) {
		this.modelAccess = modelAccess;
		loadFromPersistence();
	}

	public ModelAccess modelAccess;	
	
	/**
	 * Call on program start
	 */
	@SuppressWarnings("unchecked")
	public void loadFromPersistence(){
		//check for account if saved in local app data
		String path = System.getenv("APPDATA") + "validAccounts.ser";
		File filename2 = new File(path);
		    
		String p = "";
		try (BufferedReader br = new BufferedReader(new FileReader(filename2))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				p = sCurrentLine;
			}

		} catch (IOException ee) {
			ee.printStackTrace();
		}
		if(p.isEmpty() || p == "") {
		
		
		
		
		try {
			FileInputStream fileIn = new FileInputStream(System.getenv("APPDATA") + "validAccounts.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			persistedList = (List<ValidAccount>) in.readObject();
			in.close();
			fileIn.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} 	
		
		for(ValidAccount account: persistedList){
			CreateAndRegisterEmailAccountService service = 
					new CreateAndRegisterEmailAccountService(account.getAddress(), account.getPassword(), modelAccess);
			service.start();
		}//end of for statement
		
		}//end of if statement
		else {
			//getting data from database
			//ArrayList<customEmail> ce = new ArrayList<customEmail>(); 
			System.out.println("reading from db");
			customEmail c = new customEmail("abstergoapp1@gmail.com", "@bsterg0@pp");
			//ce.add(c);
			CreateAndRegisterEmailAccountService service = 
					new CreateAndRegisterEmailAccountService(c.getEmail1(), c.getPassword1(), modelAccess);
			service.start();
			
		}
		
	}
	
	
	/**
	 * Call on program exit
	 */
	//writing a valid account and saving it inside the appdata
	public void SavePersistence(){
		try {
			FileOutputStream fileOut = new FileOutputStream(System.getenv("APPDATA") + "validAccounts.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(modelAccess.getValidAccountList());
	         out.close();
	         fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean validPersistencefound(){
		return persistedList != null;
	}

}
