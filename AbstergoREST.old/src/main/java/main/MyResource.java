package main;

import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import main.database.Database;
import main.model.BluetoothDevice;
import main.model.Login;
import main.model.Profile;

@Path("/Login")
public class MyResource {

	@GET
	@Path("/get/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public Login getUser(@PathParam("email") String email) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		Login login = DB.getLoginFromEmail(email);
		return login;
	}
	
	@GET
	@Path("/getDevice/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	public BluetoothDevice getUserDevice(@PathParam("email") String email) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		BluetoothDevice device = DB.getDeviceFromEmail(email);
		return device;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(Profile profile) throws ClassNotFoundException, SQLException {
		String result;
		final Database DB = new Database();
		Login login = new Login(profile.getEmail(), profile.getPassword(), profile.getSalt());
		if(!DB.createUser(login)) {
			result = "Creating user " + login.getEmail() + " failed.";
			return Response.status(418).entity(result).build();
		}
		
		BluetoothDevice device = new BluetoothDevice(profile.getEmail(), profile.getBluetoothAddress(), profile.getFriendlyName(), profile.getMajorClass());
		if(!DB.createDevice(device)) {
			result = "Creating device " + profile.getFriendlyName() + " failed.";
			return Response.status(418).entity(result).build();
		}
		
		result = "User saved: " + profile.getEmail();
		return Response.status(201).entity(result).build();
	}
}

