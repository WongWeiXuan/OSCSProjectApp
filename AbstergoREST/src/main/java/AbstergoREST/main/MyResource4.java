package AbstergoREST.main;

import java.sql.SQLException;

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
import AbstergoREST.main.model.KeyPair;

@Path("/KeyPair")
public class MyResource4 {

	@GET
	@Path("private/{email}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPrivateKey(@PathParam("email") String email) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getPrivateKey(email);
	}
	
	@GET
	@Path("public/{email}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPublicKey(@PathParam("email") String email) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getPublicKey(email);
	}
	
	@GET
	@Path("encryption/{email}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getEncryptionKey(@PathParam("email") String email) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getEncryptionKey(email);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createKeys(KeyPair keyPair) throws ClassNotFoundException, SQLException {
		String result;
		final Database DB = new Database();
		if(!DB.createKeys(keyPair)) {
			result = "Creating Keys " + keyPair.getEmail() + " failed.";
			return Response.status(418).entity(result).build();
		}
		
		result = "User saved: " + keyPair.getEmail();
		return Response.status(201).entity(result).build();
	}
}