package de.rincewind.dmxc.system.environment;

import java.io.IOException;

import de.rincewind.dmxc.common.Console;
import de.rincewind.dmxc.common.util.NativeUtil;

public class DMXInterface {
	
	static {
		try {
			System.loadLibrary("DMXInterface");
		} catch (UnsatisfiedLinkError error) {
			Console.println("Cant find library in filesystem => searching in JAR");
			
			try {
				NativeUtil.loadLibraryFromJar("/nativelib/libDMXInterface.so");
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	public native int openInterface();

	public native void closeInterface();

	public native void sendData(short dmxAddress, short value);
	
}
