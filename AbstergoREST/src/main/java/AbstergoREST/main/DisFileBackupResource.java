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
import AbstergoREST.main.model.DisFileBackup;

@Path("/DisFileBackup")
public class DisFileBackupResource {

	@POST
	@Path("/insert")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertDisFileBackup(DisFileBackup dfb) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		DB.insertDisFileBackup(dfb.getUsername(), dfb.getFileName(), dfb.getFileType());
		String output = "Success!";
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/get/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<DisFileBackup> getDisFileBackup(@PathParam("username") String username) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getDisFileBackup(username);
	}
}
