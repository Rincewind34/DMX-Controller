package de.rincewind.dmxc.system.environment;

import java.io.IOException;

import de.rincewind.dmxc.common.util.NativeUtil;

public class DMXInterface {
	
	static {
//		try {
//			System.loadLibrary("DMXInterface");
//		} catch (UnsatisfiedLinkError error) {
			try {
				NativeUtil.loadLibraryFromJar("/nativelib/libDMXInterface.so");
			} catch (IOException e) {
				e.printStackTrace();
			}
//		}
	}
	
	public native int openInterface();

	public native void closeInterface();

	public native void sendData(short dmxAddress, short value);
	
}
