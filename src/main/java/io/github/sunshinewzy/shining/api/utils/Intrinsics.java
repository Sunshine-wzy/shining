package io.github.sunshinewzy.shining.api.utils;

public class Intrinsics {
	
	private Intrinsics() {}

	public static void checkNotNull(Object object) {
		if (object == null) {
			throw new NullPointerException();
		}
	}

	public static void checkNotNull(Object object, String message) {
		if (object == null) {
			throw new NullPointerException(message);
		}
	}
	
}
