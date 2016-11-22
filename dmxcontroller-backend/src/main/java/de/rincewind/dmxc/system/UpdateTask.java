package de.rincewind.dmxc.system;

public class UpdateTask implements Runnable {
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			long current = System.currentTimeMillis();
			
			Main.environment().updateChangedValues();
			
			long sleep = 50 - (System.currentTimeMillis() - current);
			
			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					// No printing
				}
			}
		}
	}

}
