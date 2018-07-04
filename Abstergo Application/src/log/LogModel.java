package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util.EventLogType;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.EVENTLOGRECORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class LogModel {
	private File encryptedFile;
	private String logName;
	private HANDLE handle;
	private boolean opened;

	public LogModel() {
		this.encryptedFile = null;
		this.opened = false;
	}

	public LogModel(String logName) {
		super();
		this.logName = logName;
	}

	public LogModel(File encryptedFile, String logName) {
		this.encryptedFile = encryptedFile;
		this.logName = logName;
	}

	public boolean openEventLog() {
		if (logName != null && !logName.isEmpty()) {
			this.handle = Advapi32.INSTANCE.OpenEventLog(null, logName);
			if (handle != null) {
				opened = true;
				return true;
			} else {
				throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
			}
		}
		return false;
	}

	public boolean closeEventLog() {
		opened = false;
		return Advapi32.INSTANCE.CloseEventLog(this.handle);
	}

	private File writeToFile(ArrayList<ArrayList<String>> stringArray, String fileName) {
		try {
			File temp = File.createTempFile(fileName, ".txt");
			FileOutputStream fos = new FileOutputStream(temp, true);
			Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(writer);
			PrintWriter out = new PrintWriter(bw);
			for (ArrayList<String> array : stringArray) {
				boolean first = true;

				for (String s : array) {
					if (first) {
						out.append(s);
						first = false;
					} else {
						out.append(";" + s);
					}
				}
				out.append("\n");
			}
			out.flush();
			out.close();
			bw.close();
			writer.close();
			fos.close();

			return temp;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<String> getRecordSettings(EVENTLOGRECORD record, Pointer pevlr) {
		// TODO Add settings
		// TODO Change stringArray to <Object> instead of ArrayList<String>
		ArrayList<String> stringArray = new ArrayList<String>();
		stringArray.add("Event Category: " + record.EventCategory.intValue());
		stringArray.add("Event ID: " + (record.EventID.intValue() & 0xFFFF));
		stringArray.add("Event Source: " + pevlr.getWideString(record.size()));
		stringArray.add("Event Type: " + getType(record.EventType.intValue()).toString());
		stringArray.add("Record Number: " + record.RecordNumber);
		stringArray.add("Event Time Generated: " + record.TimeGenerated.intValue());
		stringArray.add("Event Time Written: " + record.TimeWritten.intValue());
		stringArray.add("Event Data: " + getString(record, pevlr));
		return stringArray;
	}

	public int getNewestID(File encrypted, String key) {
		try {
			AESEncryption aes = new AESEncryption(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)));
			File file = new File(logName + "AES.txt");
			File decryptedFile = aes.decryptFile(file);

			RandomAccessFile fileHandler = new java.io.RandomAccessFile(decryptedFile, "r");
			long fileLength = fileHandler.length() - 1;
			StringBuilder sb = new StringBuilder();
			int line = 0;

			for (long filePointer = fileLength; filePointer != -1; filePointer--) {
				fileHandler.seek(filePointer);
				int readByte = fileHandler.readByte();

				if (readByte == 0xA) {
					if (filePointer < fileLength) {
						line = line + 1;
					}
				} else if (readByte == 0xD) {
					if (filePointer < fileLength - 1) {
						line = line + 1;
					}
				}
				if (line >= 1) {
					break;
				}
				sb.append((char) readByte);
			}

			String lastLine = sb.reverse().toString();
			System.out.println("Last line: " + lastLine);
			fileHandler.close();

			Scanner sc = new Scanner(lastLine);
			sc.useDelimiter(";");

			while (sc.hasNext()) {
				String s = sc.next();
				if ("Record Number".equals(s.substring(0, 13))) {
					sc.close();
					System.out.println("Last record: " + s.substring(15));
					return Integer.parseInt(s.substring(15));
				}
			}
			sc.close();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public File read(String aesKey) {
		File file = null;
		boolean stop = false;
		int recordNumLimit = 0;
		if (encryptedFile != null && encryptedFile.exists()) {
			recordNumLimit = getNewestID(encryptedFile, aesKey);
		}
		IntByReference pnBytesRead = new IntByReference();
		IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
		Memory buffer = new Memory(1024 * 64);

		ArrayList<ArrayList<String>> stringArrayArray = new ArrayList<ArrayList<String>>();
		while (true) {
			if ("System".equals(logName)) {
				if (!Advapi32.INSTANCE.ReadEventLog(handle,
						WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_BACKWARDS_READ, 0, buffer, (int) buffer.size(),
						pnBytesRead, pnMinNumberOfBytesNeeded) && !stop) {
					int dwRead = pnBytesRead.getValue();
					Pointer pevlr = buffer;
					while (dwRead > 0) {
						EVENTLOGRECORD record = new EVENTLOGRECORD(pevlr);
						if (recordNumLimit < record.RecordNumber.intValue()) {
							stringArrayArray.add(0, getRecordSettings(record, pevlr));
							dwRead -= record.Length.intValue();
							pevlr = pevlr.share(record.Length.intValue());
						} else {
							stop = true;
							break;
						}
					}
				} else {
					file = writeToFile(stringArrayArray, logName);
					break;
				}
			} else {
				if (Advapi32.INSTANCE.ReadEventLog(handle,
						WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_BACKWARDS_READ, 0, buffer, (int) buffer.size(),
						pnBytesRead, pnMinNumberOfBytesNeeded) && !stop) {
					int dwRead = pnBytesRead.getValue();
					Pointer pevlr = buffer;
					while (dwRead > 0) {
						EVENTLOGRECORD record = new EVENTLOGRECORD(pevlr);
						if (recordNumLimit < record.RecordNumber.intValue()) {
							stringArrayArray.add(0, getRecordSettings(record, pevlr));
							dwRead -= record.Length.intValue();
							pevlr = pevlr.share(record.Length.intValue());
						} else {
							stop = true;
							break;
						}
					}
				} else {
					file = writeToFile(stringArrayArray, logName);
					break;
				}
			}
		}

		return file;
	}

	private String getString(EVENTLOGRECORD record, Pointer pevlr) {
		String[] ss = null;
		// Data
		if (record.DataLength.intValue() > 0) {
			byte[] data = pevlr.getByteArray(record.DataOffset.intValue(), record.DataLength.intValue());
			String[] stringArray = new String[1];
			stringArray[0] = new String(data, StandardCharsets.UTF_8);
			ss = stringArray;
		}
		// Strings
		if (record.NumStrings.intValue() > 0) {
			ArrayList<String> strings = new ArrayList<String>();
			int count = record.NumStrings.intValue();
			long offset = record.StringOffset.intValue();
			while (count > 0) {
				String s = pevlr.getWideString(offset);
				strings.add(s);
				offset += s.length() * Native.WCHAR_SIZE;
				offset += Native.WCHAR_SIZE;
				count--;
			}
			ss = strings.toArray(new String[0]);
		}
		if (ss != null) {
			StringBuffer buffer = new StringBuffer();
			for (String s : ss) {
				buffer.append(s);
			}
			String tempString = buffer.toString();
			tempString = tempString.replaceAll("[\\t\\n\\r]+", " ");
			return tempString;
		} else {
			return "";
		}
	}

	private EventLogType getType(int eventType) {
		switch (eventType) {
		case WinNT.EVENTLOG_SUCCESS:
			return EventLogType.Informational;
		case WinNT.EVENTLOG_INFORMATION_TYPE:
			return EventLogType.Informational;
		case WinNT.EVENTLOG_AUDIT_FAILURE:
			return EventLogType.AuditFailure;
		case WinNT.EVENTLOG_AUDIT_SUCCESS:
			return EventLogType.AuditSuccess;
		case WinNT.EVENTLOG_ERROR_TYPE:
			return EventLogType.Error;
		case WinNT.EVENTLOG_WARNING_TYPE:
			return EventLogType.Warning;
		default:
			throw new RuntimeException("Invalid type: " + eventType);
		}
	}

	public boolean isOpened() {
		return opened;
	}

	public static void main(String[] args) throws IOException {
		String key = "l2nMvmLRUqY7JeQZgD9nHQ==";
		AESEncryption aes = new AESEncryption(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)));

		File file = new File("ApplicationAES.txt");
		LogModel log = new LogModel(file, "Application");
		// log.openEventLog();
		// File file = log.read(key);
		// log.closeEventLog();

		File encrypted = aes.decryptFile(file);
		AESEncryption.writeToFile(encrypted, log.logName + "DECRYPTED.txt");
	}

}
