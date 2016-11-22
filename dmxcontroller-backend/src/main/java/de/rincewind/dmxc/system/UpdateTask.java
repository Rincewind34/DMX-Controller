package de.rincewind.dmxc.system;

public class UpdateTask implements Runnable {
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			long current = System.currentTimeMillis();
			
			Main.environment().updateChangedValues();
			
			try {
				Thread.sleep(50 - (System.currentTimeMillis() - current));
			} catch (InterruptedException e) {
				// No printing
			}
		}
	}

}
