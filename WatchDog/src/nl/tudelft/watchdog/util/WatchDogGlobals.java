package nl.tudelft.watchdog.util;

/**
 * Globals for the current WatchDog instance.
 */
public class WatchDogGlobals {

	/** A text used in the UI if WatchDog is running. */
	public final static String activeWatchDogUIText = "WatchDog is active and recording ...";

	/** A text used in the UI if WatchDog is not running. */
	public final static String inactiveWatchDogUIText = "WatchDog is inactive!";

	/** The default URI of the WatchDogServer. */
	public final static String DEFAULT_SERVER_URI = "http://watchdog.testroots.org/";

	/** Flag determining whether WatchDog is active. */
	public static boolean isActive = false;

	/** Whether the last interval transmission succeeded or failed. */
	public static boolean lastTransactionFailed = false;

	/** The client's version, as set in pom.xml. */
	public final static String CLIENT_VERSION = "1.2.0";

}
