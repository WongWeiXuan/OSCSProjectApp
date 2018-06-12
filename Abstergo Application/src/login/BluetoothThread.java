package login;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;

public class BluetoothThread implements Runnable{
	private volatile boolean running;
	private BluetoothDevice device;
	
	public BluetoothThread(BluetoothDevice device) {
		running = true;
		this.device = device;
	}
	
	@Override
	public void run() {
		try {
			ArrayList<BluetoothDevice> array = new ArrayList<BluetoothDevice>();
			array.add(device);
			if(!LoginBluetoothModel.isInitialized()) {
				LoginBluetoothModel.initialiseBluetooth();
			}
			LoginBluetoothModel.setPairedArray(array);
			
			while(running) {
				try {
					System.out.println("Running");
					boolean exist = LoginBluetoothModel.scanForPairedBluetoothDevice();
					if(!exist) {
						System.out.println("Device not found!");
						BluetoothThreadModel.stopThread();
						//BluetoothThreadModel.logoutNotFound(); // Disabling it until presentation
					}
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (BluetoothStateException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	// Method to stop the thread
    public synchronized void stop() {
        running = false;
    }
}
