package AbstergoREST.main;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import AbstergoREST.main.database.Database;
import AbstergoREST.main.model.FileHash;

@Path("/FileHash")
public class MyResource2 {

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<FileHash> getFileHashes() throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getFileHash();
	}
	
	@GET
	@Path("/get/{fileName}")
	@Produces(MediaType.APPLICATION_JSON)
	public FileHash getFileHashFromFileName(@PathParam("fileName") String fileName) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getFileHashFromFileName(fileName);
	}
	
	// For development phrase only
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateFileHashes(Map<String, String> fileHashMap) throws ClassNotFoundException, SQLException {
		String result;
		final Database DB = new Database();
		for (Map.Entry<String, String> entry : fileHashMap.entrySet()){
		    FileHash hash = new FileHash(entry.getKey(), entry.getValue());
		    if(!DB.updateFileHash(hash)) {
				result = "Updating hash for " + hash.getFileName() + " failed.";
				return Response.status(418).entity(result).build();
			}
		}
			
		result = "Update successful";
		return Response.status(201).entity(result).build();
	}
}