package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import main.model.BluetoothDevice;
import main.model.FileHash;
import main.model.Login;

public class Database {
	String login = "AbstergoLogin";
	String password = "AbstergoLogin";
	String url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=Abstergo";

	Connection conn = null;

	public Database() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		conn = DriverManager.getConnection(url, login, password);
	}
	
	private boolean executeUpdate(PreparedStatement ppstmt){
		try {
			int count = ppstmt.executeUpdate();
		
			if (count == 0) {
				System.out.println("!!! Update failed !!!\n");
				return false;
			} else {
				System.out.println("!!! Update successful !!!\n");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// User
	public Login getLoginFromEmail(String email) throws SQLException {
		String sqlline = "SELECT * FROM Login WHERE Email = ?";
		PreparedStatement ps = conn.prepareStatement(sqlline);
		ps.setString(1, email);

		ResultSet rs =	ps.executeQuery();
		while(rs.next()) {
			String password = rs.getString("Password");
			String salt = rs.getString("Salt");
			Login login = new Login(email, password, salt);
			return login;
		}
		
		return null;
	}
	
	public boolean createUser(Login login) throws SQLException{
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO Login(Email, Password, Salt) VALUES(?,?,?);");
		ppstmt.setString(1, login.getEmail());
		ppstmt.setString(2, login.getPassword());
		ppstmt.setString(3, login.getSalt());
		System.out.println("Password: " + login.getPassword());
		System.out.println("Salt: " + login.getSalt());
		return executeUpdate(ppstmt);
	}

	// Device
	public BluetoothDevice getDeviceFromEmail(String email) throws SQLException {
		String sqlline = "SELECT * FROM Device WHERE Email = ?";
		PreparedStatement ps = conn.prepareStatement(sqlline);
		ps.setString(1, email);

		ResultSet rs =	ps.executeQuery();
		while(rs.next()) {
			String bluetoothAddress = rs.getString("BluetoothAddress");
			String deviceName = rs.getString("DeviceName");
			String majorClass = rs.getString("MajorClass");
			BluetoothDevice device = new BluetoothDevice(email, bluetoothAddress, deviceName, majorClass);
			return device;
		}
		
		return null;
	}
	
	public boolean createDevice(BluetoothDevice device) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO Device(Email, BluetoothAddress, DeviceName, MajorClass) VALUES(?,?,?,?);");
		ppstmt.setString(1, device.getEmail());
		ppstmt.setString(2, device.getBluetoothAddress());
		ppstmt.setString(3, device.getFriendlyName());
		ppstmt.setString(4, device.getMajorClass());
		
		return executeUpdate(ppstmt);
	}
	
	// FileHash
	public ArrayList<FileHash> getFileHash() throws SQLException {
		ArrayList<FileHash> fileHashArray = new ArrayList<FileHash>();
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM FileHash;");
		ResultSet rs =	ppstmt.executeQuery();
		while(rs.next()) {
			String fileName = rs.getString("FileName");
			String fileSHA1 = rs.getString("FileSHA1");
			FileHash fileHash = new FileHash(fileName, fileSHA1);
			fileHashArray.add(fileHash);
		}
		
		return fileHashArray;
	}
	
	public FileHash getFileHashFromFileName(String fileNames) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM FileHash WHERE FileName = ?;");
		ppstmt.setString(1, fileNames);
		ResultSet rs =	ppstmt.executeQuery();
		FileHash fileHash = null;
		while(rs.next()) {
			String fileName = rs.getString("FileName");
			String fileSHA1 = rs.getString("FileSHA1");
			fileHash = new FileHash(fileName, fileSHA1);
		}
		
		return fileHash;
	}
	
	public boolean updateFileHash(FileHash fileHash) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("UPDATE FileHash SET FileSHA1 = ? WHERE FileName = ?;");
//		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO FileHash(FileName,FileSHA1) VALUES (?,?);");
		ppstmt.setString(2, fileHash.getFileName());
		ppstmt.setString(1, fileHash.getFileSHA1());
		
		System.out.println("NAME: " + fileHash.getFileName() + " > SHA: " + fileHash.getFileSHA1());
		return executeUpdate(ppstmt);
	}
		
}