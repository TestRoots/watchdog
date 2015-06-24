package nl.tudelft.watchdog.util;

import com.google.gson.annotations.SerializedName;

/**
 * Globals for the current WatchDog instance.
 */
public class WatchDogGlobals {

	/** A text used in the UI if WatchDog is running. */
	public final static String ACTIVE_WATCHDOG_TEXT = "WatchDog is active and recording ...";

	/** A text used in the UI if WatchDog is not running. */
	public final static String INACTIVE_WATCHDOG_TEXT = "WatchDog is inactive!";

	/** The default URI of the WatchDogServer. */
	public final static String DEFAULT_SERVER_URI = "http://watchdog.testroots.org/";

	/** Flag determining whether WatchDog is active. */
	public static boolean isActive = false;

	/** Whether the last interval transmission succeeded or failed. */
	public static boolean lastTransactionFailed = false;

	/** The client's version, as set in pom.xml. */
	public final static String CLIENT_VERSION = "0.9.1";

    /** The host ide this plugin is running on. */
    public final static IDE hostIDE = IDE.INTELLIJ;

    /** Describes the different supported IDE plugin hosts. */
    public enum IDE {
        /** Eclipse-IDE */
        @SerializedName("ec")
        ECLIPSE,

        /** IntelliJ-IDE */
        @SerializedName("ij")
        INTELLIJ
    }

}
