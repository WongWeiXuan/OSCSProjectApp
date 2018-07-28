package AbstergoREST.main;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("/Download")
public class MyResource3 {

	private String getURL(String fileName) {
		fileName = fileName.replace("~", "/");
		return "https://github.com/WongWeiXuan/OSCSProjectApp/raw/WxBranch/Abstergo%20Application/" + fileName;
	}
	
	@GET
	@Path("/{fileName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFileHashes(@PathParam("fileName") String fileName) throws SQLException, ClassNotFoundException {
		String url = getURL(fileName);
		System.out.println(url);
		StreamingOutput stream = new StreamingOutput() {
		    @Override
		    public void write(OutputStream os) throws IOException,
		    WebApplicationException {
		        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

		        URL website = new URL(url);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				ByteBuffer buffer = ByteBuffer.allocate(1);
				while (buffer.hasRemaining()) {
					rbc.read(buffer);
					buffer.flip();
					byte[] bytes = buffer.array().clone();
					writer.write(new String(bytes, "UTF-8"));
				}
		        writer.flush();
		        writer.close();
		        rbc.close();
		    }
		};
		
		return Response.ok(stream).build();
	}
}