package login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.intel.bluetooth.RemoteDeviceHelper;

public class LoginBluetoothModel {
	// Locks
	private static Object lock = new Object();
	//private static Object lock2 = new Object();
	// Storing RemoteDevice List & URL List
	private static Vector<RemoteDevice> vector = new Vector<RemoteDevice>();
	// DiscoveryAgent & DiscoveryListener
	private static DiscoveryAgent agent;
	private static DiscoveryListener listener;
	// Url attribute
	final private static int URL_ATTRIBUTE = 0x0100;
	// Paired Device
	private static ArrayList<BluetoothDevice> pairedArray = new ArrayList<BluetoothDevice>();
	// Found Variable
	private static boolean found = false;
	
	public LoginBluetoothModel() throws BluetoothStateException, FileNotFoundException {
		initialiseBluetooth();
	}
	
	public static void initialiseBluetooth() throws BluetoothStateException, FileNotFoundException {
		if(LoginDAO.getPairedDevice() != null) {
			pairedArray = LoginDAO.getPairedDevice();
		}
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		localDevice.setDiscoverable(DiscoveryAgent.LIAC);
		agent = localDevice.getDiscoveryAgent();
	}
	
	public static RemoteDevice deviceSelectionQuery() {
		System.out.println("Device Inquiry Completed.");
		System.out.print("\nSelect a device to scan: ");
		Scanner sc = new Scanner(System.in);
		int selected = Integer.parseInt(sc.next()) - 1;
		
		while(selected >= vector.size() || selected < 0) {
			System.out.print("\nSelection out of bound. Please select again: ");
			selected = Integer.parseInt(sc.next()) - 1;
		}
		
		sc.close();
		
		return vector.get(selected);
	}
	
	public static RemoteDevice deviceSelectionQuery(RemoteDevice[] devices) {
		System.out.println("Device Inquiry Completed.");
		System.out.print("\nSelect a device to scan: ");
		Scanner sc = new Scanner(System.in);
		int selected = Integer.parseInt(sc.next()) - 1;
		
		while(selected >= devices.length || selected < 0) {
			System.out.print("\nSelection out of bound. Please select again: ");
			selected = Integer.parseInt(sc.next()) - 1;
		}
		
		sc.close();
		
		return devices[selected];
	}
	
	public static BluetoothDevice deviceSelectionQuery(int hi) {
		System.out.println("Device Inquiry Completed.");
		System.out.print("\nSelect a device to scan: ");
		Scanner sc = new Scanner(System.in);
		int selected = Integer.parseInt(sc.next()) - 1;
		
		while(selected >= pairedArray.size() || selected < 0) {
			System.out.print("\nSelection out of bound. Please select again: ");
			selected = Integer.parseInt(sc.next()) - 1;
		}
		
		sc.close();
		
		return pairedArray.get(selected);
	}
	
	public static boolean scanForPairedBluetoothDevice() throws BluetoothStateException, InterruptedException {
		found = false;
		listener = new DiscoveryListener() {
		    @Override
		    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
	    		for(BluetoothDevice rd: pairedArray) {
	    			if(rd.getBluetoothAddress().equals(btDevice.getBluetoothAddress())) {
	    				found = true;
	    			}
	    		}
		    }
		 
		    @Override
		    public void inquiryCompleted(int arg0) {
		        synchronized(lock) {
		            lock.notify();
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
		
		synchronized(lock) {
			lock.wait();
		}
		
		return found;
	}
	
	public static boolean scanAndPairBluetoothDevice() throws InterruptedException, IOException {
		listener = new DiscoveryListener() {
		    @Override
		    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass arg1) {
		        String name;
		        try {
		            name = btDevice.getFriendlyName(false);
		            vector.add(btDevice);
		        } catch (Exception e) {
		            name = btDevice.getBluetoothAddress();
		        }
		         
		        System.out.println("device found: " + name);
		    }
		 
		    @Override
		    public void inquiryCompleted(int arg0) {
		        synchronized(lock) {
		            lock.notify();
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
		
		synchronized(lock) {
			lock.wait();
		}
		
		RemoteDevice rd = deviceSelectionQuery();
		boolean paired = RemoteDeviceHelper.authenticate(rd, "1234");
		Thread.sleep(1000);
		if(paired) {
			RemoteDeviceHelper.removeAuthentication(rd);
			BluetoothDevice bd = new BluetoothDevice(rd.getBluetoothAddress(), rd.getFriendlyName(true));
			pairedArray.add(bd);
			LoginDAO.addPairedDevice(pairedArray);
		}
		return paired;
	}
	
	public static void scanAndUnpairBluetoothDevice() throws IOException {
		for(BluetoothDevice bd: pairedArray) {
			System.out.println(bd.getBluetoothAddress() + ": " + bd.getFriendlyName());
		}
		
		BluetoothDevice bd = deviceSelectionQuery(1);
		LoginDAO.removePairedDevice(bd);
	}
	
	/*
	public static boolean scanAndUnpairBluetoothDevice() throws InterruptedException, IOException{
		RemoteDevice[] pairedDevice = agent.retrieveDevices(DiscoveryAgent.PREKNOWN);
		if(pairedDevice != null) {
			for(RemoteDevice rd: pairedDevice) {
				System.out.println("Device found: " + rd.getFriendlyName(true));
			}
			
			RemoteDevice rd = deviceSelectionQuery(pairedDevice);
			try {
				RemoteDeviceHelper.removeAuthentication(rd);
			}catch(IOException e) {
				return false;
			}
			
			System.out.println(rd.getFriendlyName(true) + " is successfully unpaired.");
			return true;
		}else {
			System.out.println("No paired device found!");
			return false;
		}
	}
	/*
	
	/*
	@SuppressWarnings("unused")
	private static void searchServices(RemoteDevice device) throws IOException {
		UUID[] uuidSet = new UUID[1];
		//uuidSet[0] = new UUID(0x1105); //OBEX Object Push service
		uuidSet[0] = new UUID(0x0003); //OBEX Object Push service
		//uuidSet[0] = new UUID(0x1106); //OBEX Object Push service
		//uuidSet[0] = new UUID(0x111F); //Handsfree Audio service
		//uuidSet[0] = new UUID(0x112C); //OBEX Object Push service
		int[] attrIDs = new int[]{URL_ATTRIBUTE};
	    System.out.println("Search service on '" + device.getBluetoothAddress() + ": " + device.getFriendlyName(false) + "...");
		agent.searchServices(attrIDs, uuidSet, device, listener);
	}
	*/
	
	public static void main(String[] args) throws InterruptedException, IOException {
		LoginBluetoothModel.initialiseBluetooth();
		//LoginBluetoothModel.scanAndPairBluetoothDevice();
		//LoginBluetoothModel.scanAndUnpairBluetoothDevice();
		
		while(true) {
			if(LoginBluetoothModel.scanForPairedBluetoothDevice()) {
				System.out.println("Active connection");
				Thread.sleep(1000);
			}else {
				System.out.println("Connection lost with paired device!");
			}
		}
		
	}
}
