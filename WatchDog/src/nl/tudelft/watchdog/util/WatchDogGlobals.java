package nl.tudelft.watchdog.util;

/**
 * Globals for the current WatchDog instance.
 */
public class WatchDogGlobals {

	/** A text used in the UI if WatchDog is running. */
	public final static String activeWatchDogUIText = "WatchDog is active and recording ...";

	/** A text used in the UI if WatchDog is not running. */
	public final static String inactiveWatchDogUIText = "WatchDog is inactive!";

	/** The URI of the WatchDogServer. */
	public final static String watchDogServerURI = "http://www.testroots.org/watchdog/";

	/** Flag determining whether WatchDog is active. */
	public static boolean isActive = false;

	/** The reading timeout in milliseconds. */
	public static int READING_TIMEOUT = 5000;

	/** The typing timeout in milliseconds. */
	public static int TYPING_TIMEOUT = 3000;
}
