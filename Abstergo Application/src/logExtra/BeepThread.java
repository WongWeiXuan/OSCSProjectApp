package logExtra;

import log.controller.LogNetworkPageController;

public class BeepThread implements Runnable {
	private volatile static boolean running;
	
	public BeepThread() {
		running = true;
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				for(String s :LogNetworkPageController.listArray) {
					LogNetworkPageController.beep(s);
					Thread.sleep(20);
					HandshakeThread.sendBeeps(s);
				}
			
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static boolean isRunning() {
		return running;
	}

	// Method to stop the thread
	public synchronized static void stop() {
		running = false;
	}
}
