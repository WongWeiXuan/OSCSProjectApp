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
import AbstergoREST.main.model.FileBackup;

@Path("/FileBackup")
public class FileBackupResource {

	@GET
	@Path("/getuserbackupfiles/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<FileBackup> getUserBackupFiles(@PathParam("username") String username) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.getUserBackupFiles(username);
	}
	
	@GET
	@Path("/getfileverhist/{username}/{filebackupindex}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<FileBackup> getFileVerHist(@PathParam("username") String username, @PathParam("filebackupindex") String fileBackupIndex) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		return DB.getFileVerHist(username, fileBackupIndex);
	}
	
	@POST
	@Path("/backupfile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response backupFile(FileBackup fb) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		DB.backupFile(fb.getUsername(), fb.getFileName(), fb.getFileType(), fb.getFileData(), fb.getDateCreated(), fb.getFileBackupIndex());
		String output = "Success!";
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/downloadbackupfile/{username}/{filename}")
	@Produces(MediaType.APPLICATION_JSON)
	public FileBackup downloadBackupFile(@PathParam("username") String username, @PathParam("filename") String fileName) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		return DB.downloadBackupFile(username, fileName);
	}
}
