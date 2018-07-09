package bluetooth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import com.intel.bluetooth.BlueCoveImpl;
import com.intel.bluetooth.RemoteDeviceHelper;

import login.LoginDAO;

public class LoginBluetoothModel {
	// Locks
	private static Object lock = new Object();
	// Storing RemoteDevice List & URL List
	private static Vector<RemoteDevice> vector = new Vector<RemoteDevice>();
	private static Map<RemoteDevice, String> deviceListing = new HashMap<RemoteDevice, String>();
	// DiscoveryAgent & DiscoveryListener
	private static DiscoveryAgent agent;
	private static DiscoveryListener listener;
	// Paired Device
	private static ArrayList<BluetoothDevice> pairedArray = new ArrayList<BluetoothDevice>();
	// Found Variable
	private static boolean found = false;
	// BluetoothDeviceFilter
	private static int[] filter = { 512, 1792, 7936 };
	private static boolean initialized = false;

	public LoginBluetoothModel() throws BluetoothStateException, FileNotFoundException {
		initialiseBluetooth();
	}

	public static void initialiseBluetooth() throws BluetoothStateException, FileNotFoundException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		localDevice.setDiscoverable(DiscoveryAgent.LIAC);
		agent = localDevice.getDiscoveryAgent();
		BlueCoveImpl.setConfigProperty("bluecove.inquiry.duration", "2");
		BlueCoveImpl.setConfigProperty("bluecove.inquiry.report.asap", "true");
		BlueCoveImpl.setConfigProperty("bluecove.connect.timeout", "20000");

		initialized = true;
	}

	public static BluetoothDevice deviceSelectionQuery(int selection) {
		Scanner sc = new Scanner(System.in, "UTF-8");
		int selected = 0;
		try {
			selected = Integer.parseInt(sc.next()) - 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			sc.close();
			System.out.print("\nSelection out of bound.");
			return null;
		}
		sc.close();

		return pairedArray.get(selected);
	}

	public static Map<RemoteDevice, String> scanBluetoothDevice() throws InterruptedException, IOException {
		listener = new DiscoveryListener() {
			@Override
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
				String deviceMajor = String.valueOf(arg1.getMajorDeviceClass());
				for (int i : filter) { // Only add if it is a phone, watch or undentified
					if (Integer.parseInt(deviceMajor) == i) { // When matches
						vector.add(btDevice);
						deviceListing.put(btDevice, deviceMajor);
						break;
					}
				}
			}

			@Override
			public void inquiryCompleted(int arg0) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
			}

			@Override
			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			}
		};
		agent.startInquiry(DiscoveryAgent.LIAC, listener);

		synchronized (lock) {
			lock.wait();
		}
		return deviceListing;
	}

	public static boolean pairBluetoothDevice(int selection) throws InterruptedException, IOException {
		RemoteDevice rd = vector.get(selection);
		boolean paired = RemoteDeviceHelper.authenticate(rd, "0000");
		Thread.sleep(1000);
		if (paired) {
			RemoteDeviceHelper.removeAuthentication(rd);
			BluetoothDevice bd = new BluetoothDevice(rd.getBluetoothAddress(), rd.getFriendlyName(true),
					deviceListing.get(rd));
			pairedArray.add(bd);
		}

		return paired;
	}

	public static boolean scanForPairedBluetoothDevice() throws BluetoothStateException, InterruptedException {
		found = false;
		listener = new DiscoveryListener() {
			@Override
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
				for (BluetoothDevice rd : pairedArray) {
					if (rd.getBluetoothAddress().equals(btDevice.getBluetoothAddress())) {
						found = true;
					}
				}
			}

			@Override
			public void inquiryCompleted(int arg0) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}

			@Override
			public void serviceSearchCompleted(int arg0, int arg1) {
			}

			@Override
			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			}
		};
		agent.startInquiry(DiscoveryAgent.LIAC, listener);

		synchronized (lock) {
			lock.wait();
		}
		return found;
	}

	public static void scanAndUnpairBluetoothDevice(int selection) throws IOException {
		for (BluetoothDevice bd : pairedArray) {
			System.out.println(bd.getBluetoothAddress() + ": " + bd.getFriendlyName());
		}

		BluetoothDevice bd = deviceSelectionQuery(selection);
		LoginDAO.removePairedDevice(bd);
	}

	public static void shutdownBluetooth() {
		BlueCoveImpl.shutdown();
	}

	public static Object getLock() {
		return lock;
	}

	public static ArrayList<BluetoothDevice> getPairedArray() {
		return pairedArray;
	}

	public static void setPairedArray(ArrayList<BluetoothDevice> pairedArray) {
		LoginBluetoothModel.pairedArray = pairedArray;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	/*
	 * public static boolean scanAndUnpairBluetoothDevice() throws
	 * InterruptedException, IOException{ RemoteDevice[] pairedDevice =
	 * agent.retrieveDevices(DiscoveryAgent.PREKNOWN); if(pairedDevice != null) {
	 * for(RemoteDevice rd: pairedDevice) { System.out.println("Device found: " +
	 * rd.getFriendlyName(true)); }
	 * 
	 * RemoteDevice rd = deviceSelectionQuery(pairedDevice); try {
	 * RemoteDeviceHelper.removeAuthentication(rd); }catch(IOException e) { return
	 * false; }
	 * 
	 * System.out.println(rd.getFriendlyName(true) + " is successfully unpaired.");
	 * return true; }else { System.out.println("No paired device found!"); return
	 * false; } } /*
	 * 
	 * /*
	 * 
	 * @SuppressWarnings("unused") private static void searchServices(RemoteDevice
	 * device) throws IOException { UUID[] uuidSet = new UUID[1]; //uuidSet[0] = new
	 * UUID(0x1105); //OBEX Object Push service uuidSet[0] = new UUID(0x0003);
	 * //OBEX Object Push service //uuidSet[0] = new UUID(0x1106); //OBEX Object
	 * Push service //uuidSet[0] = new UUID(0x111F); //Handsfree Audio service
	 * //uuidSet[0] = new UUID(0x112C); //OBEX Object Push service int[] attrIDs =
	 * new int[]{URL_ATTRIBUTE}; System.out.println("Search service on '" +
	 * device.getBluetoothAddress() + ": " + device.getFriendlyName(false) + "...");
	 * agent.searchServices(attrIDs, uuidSet, device, listener); }
	 */

	/*
	 * public static void main(String[] args) throws InterruptedException,
	 * IOException { LoginBluetoothModel.initialiseBluetooth();
	 * Map<RemoteDevice,String> test = LoginBluetoothModel.scanBluetoothDevice(); //
	 * Retrieve list of devices
	 * 
	 * // Print the list of device for (Entry<RemoteDevice, String> entry :
	 * deviceListing.entrySet()) {
	 * System.out.println(entry.getKey().getFriendlyName(false));
	 * System.out.println(entry.getKey().hashCode()); }
	 * 
	 * // "Select" device //Scanner sc = new Scanner(System.in);
	 * //LoginBluetoothModel.pairBluetoothDevice(Integer.parseInt(sc.next())); //
	 * Pair it
	 * 
	 * //sc.close(); // Close scanner
	 * 
	 * // LoginBluetoothModel.scanAndUnpairBluetoothDevice();
	 * 
	 * while (true) { if (LoginBluetoothModel.scanForPairedBluetoothDevice()) {
	 * System.out.println("Active connection"); Thread.sleep(1000); } else {
	 * System.out.println("Connection lost with paired device!"); } } }
	 */
}
