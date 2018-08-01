package AbstergoREST.main;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import AbstergoREST.main.database.Database;
import AbstergoREST.main.model.BluetoothDevice;
import AbstergoREST.main.model.Email;
import AbstergoREST.main.model.KeyPair;

@Path("/Email") // "http://abstergorest.appspot.com/rest/Email"
public class MyResource5 {
	
	@GET
	@Path("encryption/{address}") // "http://abstergorest.appspot.com/rest/Email/encryption/{something}"
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Email> getEncryptionKey(@PathParam("address") String address) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getEmails(address);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createKeys(Email email) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		DB.insertEmail(email.getUsername(), email.getAddress(), email.getPassword());
	}
}