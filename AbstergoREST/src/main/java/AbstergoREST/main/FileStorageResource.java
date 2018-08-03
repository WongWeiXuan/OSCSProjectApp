package AbstergoREST.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.omg.CORBA.portable.OutputStream;

import AbstergoREST.main.database.Database;
import AbstergoREST.main.model.FileStorage;
import fileStorage.FileSplit;

@Path("/FileStorage")
public class FileStorageResource {
	
	@GET
	@Path("/getuserfiles/{username}/{filename}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<FileStorage> getUserFiles(@PathParam("username") String username, @PathParam("filename") String fileName) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		return DB.getUserFiles(username, fileName);
	}
	
	@POST
	@Path("/uploadfile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadFile(FileStorage fs) throws Exception {
		FileSplit.splitFile(fs.getFile(), fs.getFileName());
		String output = "File split and uploaded successfully.";
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/downloadfile/{username}/{filename}/{file}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(@PathParam("username") String username, @PathParam("filename") String fileName, @PathParam("file") File into) throws IOException, Exception {
		File file = FileSplit.mergeFiles(FileSplit.listOfFilesToMerge(username, fileName), into);
		StreamingOutput fileStream =  new StreamingOutput() {
			@Override
			public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
				try {
					byte[] data = Files.readAllBytes(file.toPath());
					output.write(data);
					output.flush();
				} catch (Exception e) {
					
				}
			}
		};
		return Response.ok(fileStream).build();
	}
	
	@GET
	@Path("getsplitfiles/{username}/{filename}")
	@Produces(MediaType.APPLICATION_JSON)
	public FileStorage getSplitFiles(@PathParam("username") String username, @PathParam("filename") String fileName) throws ClassNotFoundException, SQLException {
		final Database DB = new Database();
		return DB.downloadFile(username, fileName);
	}
	
}
