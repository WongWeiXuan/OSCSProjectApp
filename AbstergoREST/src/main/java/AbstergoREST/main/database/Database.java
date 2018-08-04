package AbstergoREST.main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import AbstergoREST.main.model.BluetoothDevice;
import AbstergoREST.main.model.DisFileBackup;
import AbstergoREST.main.model.Email;
import AbstergoREST.main.model.FileBackup;
import AbstergoREST.main.model.FileHash;
import AbstergoREST.main.model.FileStorage;
import AbstergoREST.main.model.KeyPair;
import AbstergoREST.main.model.Login;

public class Database {
	String url = "jdbc:mysql://google/AbstergoDB?cloudSqlInstance=abstergorest:asia-southeast1:abstergodatabase&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=abstergo&password=abstergo&useSSL=false";
	Connection conn = null;

	public Database() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(url);
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
//		PreparedStatement ppstmt = conn.prepareStatement("UPDATE FileHash SET FileSHA1 = ? WHERE FileName = ?;");
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO FileHash(FileName,FileSHA1) VALUES (?,?);");
		ppstmt.setString(2, fileHash.getFileName());
		ppstmt.setString(1, fileHash.getFileSHA1());
		
		System.out.println("NAME: " + fileHash.getFileName() + " > SHA: " + fileHash.getFileSHA1());
		return executeUpdate(ppstmt);
	}
	
	// KeyPair
	public String getPrivateKey(String email) throws SQLException {
		String privateKey = "";
		PreparedStatement ppstmt = conn.prepareStatement("SELECT PrivateKey FROM KeyPair WHERE Email = ?;");
		ppstmt.setString(1, email);
		ResultSet rs =	ppstmt.executeQuery();
		while(rs.next()) {
			privateKey = rs.getString("PrivateKey");
		}
		
		return privateKey;
	}
	
	public String getPublicKey(String email) throws SQLException {
		String publicKey = "";
		PreparedStatement ppstmt = conn.prepareStatement("SELECT PublicKey FROM KeyPair WHERE Email = ?;");
		ppstmt.setString(1, email);
		ResultSet rs =	ppstmt.executeQuery();
		while(rs.next()) {
			publicKey = rs.getString("PublicKey");
		}
		
		return publicKey;
	}

	public String getEncryptionKey(String email) throws SQLException {
		String encryptionKey = "";
		PreparedStatement ppstmt = conn.prepareStatement("SELECT EncryptionKey FROM KeyPair WHERE Email = ?;");
		ppstmt.setString(1, email);
		ResultSet rs =	ppstmt.executeQuery();
		while(rs.next()) {
			encryptionKey = rs.getString("EncryptionKey");
		}
		
		return encryptionKey;
	}
	
	public boolean createKeys(KeyPair keyPair) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO KeyPair(Email, PrivateKey, PublicKey, EncryptionKey) VALUES(?,?,?,?);");
		ppstmt.setString(1, keyPair.getEmail());
		ppstmt.setString(2, keyPair.getPrivateKey());
		ppstmt.setString(3, keyPair.getPublicKey());
		ppstmt.setString(4, keyPair.getEncryptionKey());
		
		return executeUpdate(ppstmt);
	}
	
	//FileStorage
	public ArrayList<FileStorage> getUserFiles(String username) throws SQLException {
		ArrayList<FileStorage> fileList = new ArrayList<FileStorage>();
		PreparedStatement ppstmt = conn.prepareStatement("SELECT FROM FileStorage(Username, FileName, FileType, FileSize, DateCreated) WHERE Username = ?;");
		ppstmt.setString(1, username);
		
		ResultSet rs = ppstmt.executeQuery();
		while (rs.next()) {
			String userName = rs.getString("Username");
			String fileName = rs.getString("FileName");
			String fileType = rs.getString("FileType");
			String fileSize = rs.getString("FileSize");
			String dateCreated = rs.getString("DateCreated");
			
			fileList.add(new FileStorage(userName, fileName, fileType, fileSize, dateCreated));
			
			return fileList;
		}
		return null;
	}
	
	public boolean uploadFile(String username, String fileName, String fileType, String dateCreated, byte[] splitFile1, byte[] splitFile2, byte[] splitFile3, byte[] splitFile4, byte[] keyBlock, byte[] parBlock, int noOfFiles) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO FileStorage(Username, FileName, FileType, DateCreated, SplitFile1, SplitFile2, SplitFile3, SplitFile4, KeyBlock, ParBlock, NoOfFiles) VALUES(?,?,?,?,?,?,?,?,?,?,?);");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileName);
		ppstmt.setString(3, fileType);
		ppstmt.setString(4, dateCreated);
		ppstmt.setBytes(5, splitFile1);
		ppstmt.setBytes(6, splitFile2);
		ppstmt.setBytes(7, splitFile3);
		ppstmt.setBytes(8, splitFile4);
		ppstmt.setBytes(9, keyBlock);
		ppstmt.setBytes(10, parBlock);
		ppstmt.setInt(11, noOfFiles);
		
		return executeUpdate(ppstmt);
	}
	
	public FileStorage downloadFile(String username, String fileName) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM FileStorage WHERE Username = ? AND FileName = ?;");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileName);
		
		ResultSet rs = ppstmt.executeQuery();
		while (rs.next()) {
			String userName = rs.getString("Username");
			String filename = rs.getString("FileName");
			String fileType = rs.getString("FileType");
			String fileSize = rs.getString("FileSize");
			String dateCreated = rs.getString("DateCreated");
			byte[] splitFile1 = rs.getBytes("SplitFile1");
			byte[] splitFile2 = rs.getBytes("SplitFile2");
			byte[] splitFile3 = rs.getBytes("SplitFile3");
			byte[] splitFile4 = rs.getBytes("SplitFile4");
			byte[] keyBlock = rs.getBytes("KeyBlock");
			byte[] parBlock = rs.getBytes("ParBlock");
			int noOfFiles = rs.getInt("NoOfFiles");
			
			FileStorage fs = new FileStorage(userName, filename, fileType, fileSize, dateCreated, splitFile1, splitFile2, splitFile3, splitFile4, keyBlock, parBlock, noOfFiles);
			
			return fs;
		}
		return null;
	}
	
	public boolean writeMissingFile(String username, String fileName, byte[] missingFileData, int missingFileNo) throws SQLException {
		PreparedStatement ppstmt = null;
		if (missingFileNo == 1) {
			ppstmt = conn.prepareStatement("UPDATE FileStorage SET SplitFile1 = ? WHERE Username = ? AND FileName = ?;");
		}
		else if (missingFileNo == 2) {
			ppstmt = conn.prepareStatement("UPDATE FileStorage SET SplitFile2 = ? WHERE Username = ? AND FileName = ?;");
		}
		else if (missingFileNo == 3) {
			ppstmt = conn.prepareStatement("UPDATE FileStorage SET SplitFile3 = ? WHERE Username = ? AND FileName = ?;");
		}
		else if (missingFileNo == 4) {
			ppstmt = conn.prepareStatement("UPDATE FileStorage SET SplitFile4 = ? WHERE Username = ? AND FileName = ?;");
		}
		
		ppstmt.setBytes(1, missingFileData);
		ppstmt.setString(2, username);
		ppstmt.setString(3, fileName);
		
		return executeUpdate(ppstmt);
	}
	
	public boolean deleteFile(String username, String fileName) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("DELETE FROM FileStorage WHERE Username = ? AND FileName = ?;");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileName);
		
		return executeUpdate(ppstmt);
	}
	
	//DisFileBackup
	public boolean insertDisFileBackup(String username, String fileName, String fileType) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO DisFileBackup(Username, FileName, FileType) VALUES(?,?,?);");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileName);
		ppstmt.setString(3, fileType);
		
		return executeUpdate(ppstmt);
	}
	
	public DisFileBackup getDisFileBackup(String username) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM DisFileBackup WHERE Username = ?;");
		ppstmt.setString(1, username);
		
		ResultSet rs = ppstmt.executeQuery();
		while (rs.next()) {
			String userName = rs.getString("Username");
			String fileName = rs.getString("FileName");
			String fileType = rs.getString("FileType");
			String fileSize = rs.getString("FileSize");
			
			DisFileBackup dfb = new DisFileBackup(userName, fileName, fileType, fileSize);
			
			return dfb;
		}
		return null;
	}
	
	//FileBackup
	public ArrayList<FileBackup> getUserBackupFiles(String username) throws SQLException {
		ArrayList<FileBackup> fileList = new ArrayList<FileBackup>();
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM FileBackup WHERE Username = ?;");
		ppstmt.setString(1, username);
		
		ResultSet rs = ppstmt.executeQuery();
		while (rs.next()) {
			String userName = rs.getString("Username");
			String fileName = rs.getString("FileName");
			String fileType = rs.getString("FileType");
			byte[] fileData = rs.getBytes("FileData");
			String dateCreated = rs.getString("DateCreated");
			String fileBackupIndex = rs.getString("FileBackupIndex");
			
			fileList.add(new FileBackup(userName, fileName, fileType, fileData, dateCreated, fileBackupIndex));
			
			return fileList;
		}
		return null;
	}
	
	public ArrayList<FileBackup> getFileVerHist(String username, String fileBackupIndex) throws SQLException {
		ArrayList<FileBackup> fileList = new ArrayList<FileBackup>();
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM FileBackup WHERE Username = ? AND FileBackupIndex = ?;");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileBackupIndex);
		
		ResultSet rs = ppstmt.executeQuery();
		while (rs.next()) {
			String userName = rs.getString("Username");
			String fileName = rs.getString("FileName");
			String fileType = rs.getString("FileType");
			byte[] fileData = rs.getBytes("FileData");
			String dateCreated = rs.getString("DateCreated");
			String filebackupIndex = rs.getString("FileBackupIndex");
			
			fileList.add(new FileBackup(userName, fileName, fileType, fileData, dateCreated, filebackupIndex));
			
			return fileList;
		}
		return null;
	}
	
	
	public boolean backupFile(String username, String fileName, String fileType, byte[] fileData, String dateCreated, String fileBackupIndex) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO FileBackup(Username, FileName, FileType, FileData, DateCreated, FileBackupIndex) VALUES(?,?,?,?,?,?);");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileName);
		ppstmt.setString(3, fileType);
		ppstmt.setBytes(4, fileData);
		ppstmt.setString(5, dateCreated);
		ppstmt.setString(6, fileBackupIndex);
		
		return executeUpdate(ppstmt);
	}
	
	public FileBackup downloadBackupFile(String username, String fileName) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM FileBackup WHERE Username = ? AND FileName = ?;");
		ppstmt.setString(1, username);
		ppstmt.setString(2, fileName);
		
		ResultSet rs = ppstmt.executeQuery();
		while (rs.next()) {
			String userName = rs.getString("Username");
			String filename = rs.getString("FileName");
			String fileType = rs.getString("FileType");
			byte[] fileData = rs.getBytes("FileData");
			String dateCreated = rs.getString("DateCreated");
			String fileBackupIndex = rs.getString("FileBackupIndex");
			
			FileBackup fb = new FileBackup(userName, filename, fileType, fileData, dateCreated, fileBackupIndex);
			
			return fb;
		}
		
		return null;
	}
	
	// Xuan Zheng's part
	public ArrayList<Email> getEmails(String address) throws SQLException {
		ArrayList<Email> em = new ArrayList<Email>();
		PreparedStatement ppstmt = conn.prepareStatement("SELECT * FROM Email WHERE Username = ?;");
		ppstmt.setString(1, address);
		ResultSet rs =	ppstmt.executeQuery();
		while(rs.next()) {
			em.add(new Email(rs.getString("username"), rs.getString("address"), rs.getString("password")));
		}
		
		return em;
		
	}
	
	public void insertEmail(String username, String map, String pass) throws SQLException {
		PreparedStatement ppstmt = conn.prepareStatement("INSERT INTO Email(Username, Address, Password) VALUES(?,?,?);");
		ppstmt.setString(1, username);
		ppstmt.setString(2, map);
		ppstmt.setString(3, pass);
		
		executeUpdate(ppstmt);
	}
}