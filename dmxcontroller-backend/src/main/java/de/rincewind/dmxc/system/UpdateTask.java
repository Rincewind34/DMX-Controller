package de.rincewind.dmxc.system;

public class UpdateTask implements Runnable {
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			Main.environment().updateChangedValues();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// No printing
			}
		}
	}

}
