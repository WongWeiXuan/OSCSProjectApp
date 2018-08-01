package AbstergoREST.main;

import java.io.File;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import AbstergoREST.main.model.FileStorage;
import fileStorage.FileSplit;

@Path("/FileStorage")
public class FileStorageResource {

	@Path("/uploadfile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadFile(FileStorage fs) throws Exception {
		FileSplit.splitFile(fs.getFile());
		String output = "File split and uploaded successfully.";
		return Response.status(200).entity(output).build();
	}
	
	@Path("/downloadfile")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response downloadFile(String username, String fileName) throws IOException, Exception {
		/*
		 * TODO:
		 * Merge the split files into the original file
		 */
		return Response.ok().build();
	}
	
}
