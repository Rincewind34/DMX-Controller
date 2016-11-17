package de.rincewind.dmxc.system.environment;

public enum MergingMethod {

	FIRST_LOGIN, HIGHST_VALUE;

	public static MergingMethod fromCommandLine(String commandLine) {
		if (commandLine.equals("login")) {
			return MergingMethod.FIRST_LOGIN;
		} else if (commandLine.equals("highst")) {
			return MergingMethod.HIGHST_VALUE;
		} else {
			return null;
		}
	}

	public static String toCommandLine(MergingMethod method) {
		if (method == MergingMethod.FIRST_LOGIN) {
			return "login";
		} else if (method == MergingMethod.HIGHST_VALUE) {
			return "highst";
		} else {
			return null;
		}
	}

}
