package AbstergoREST.main;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import AbstergoREST.main.database.Database;
import AbstergoREST.main.model.FileStorage;
import fileStorage.FileSplit;

@Path("/FileStorage")
public class FileStorageResource {
	
	@GET
	@Path("/getuserfiles/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<FileStorage> getUserFiles(@PathParam("username") String username) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		return DB.getUserFiles(username);
	}
	
	@POST
	@Path("/uploadfile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadFile(FileStorage fs) throws Exception {
		FileSplit.splitFile(fs.getUsername(), fs.getFileName(), fs.getFileType(), fs.getDateCreated(), fs.getFile());
		String output = "Success!";
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/getsplitfiles/{username}/{filename}")
	@Produces(MediaType.APPLICATION_JSON)
	public FileStorage getSplitFiles(@PathParam("username") String username, @PathParam("filename") String fileName) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		return DB.downloadFile(username, fileName);
	}
	
	@PUT
	@Path("/writemissingfile/{missingfileno}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response writeMissingFile(FileStorage fs, @PathParam("missingfileno") int missingFileNo) throws SQLException, ClassNotFoundException {
		final Database DB = new Database();
		if (missingFileNo == 1) {
			DB.writeMissingFile(fs.getUsername(), fs.getFileName(), fs.getSplitFile1(), missingFileNo);
		}
		else if (missingFileNo == 2) {
			DB.writeMissingFile(fs.getUsername(), fs.getFileName(), fs.getSplitFile2(), missingFileNo);
		}
		else if (missingFileNo == 3) {
			DB.writeMissingFile(fs.getUsername(), fs.getFileName(), fs.getSplitFile3(), missingFileNo);
		}
		else if (missingFileNo == 4) {
			DB.writeMissingFile(fs.getUsername(), fs.getFileName(), fs.getSplitFile4(), missingFileNo);
		}
		String output = "Success!";
		return Response.status(200).entity(output).build();
	}
	
	@DELETE
	@Path("/deletefile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteFile(FileStorage fs) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		DB.deleteFile(fs.getUsername(), fs.getFileName());
		String output = "Success!";
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/getfilenames/{username}")
	public ArrayList<FileStorage> getFileNames(@PathParam("username") String username) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		return DB.getFileNames(username);
	}
	
}
