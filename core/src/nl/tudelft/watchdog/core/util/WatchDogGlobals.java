package nl.tudelft.watchdog.core.util;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;

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
	
	public final static String DEBUG_SURVEY_TEXT = "Do you ever debug? Did you know WatchDog now also reports on debugging?";

	/** Flag determining whether WatchDog is active. */
	public static boolean isActive = false;

	/** Whether the last interval transmission succeeded or failed. */
	public static boolean lastTransactionFailed = false;

	/** The client's version, as set in pom.xml. */
	public final static String CLIENT_VERSION = "2.0.0";

	/** The host ide this plugin is running on. */
	public static IDE hostIDE;

    private static String logDirectory;

    /** Preferences for this instance of IDE */
    private static PreferencesBase preferences;
    
    /** Get WatchDog Preferences. */
	public static PreferencesBase getPreferences() {
		return preferences;
	}
	
	/** Set WatchDog Preferences. */
	public static void setPreferences(PreferencesBase preferences) {
		WatchDogGlobals.preferences = preferences;
	}

	/** Get WatchDog Log directory. */
	public static String getLogDirectory() {
		return logDirectory;
	}

	/** Set WatchDog Log directory. */
	public static void setLogDirectory(String logDirectory) {
		WatchDogGlobals.logDirectory = logDirectory;
	}
	
	public static int getUserInactivityTimeoutDuration() {
		return 16000;
	}

	/** Describes the different supported IDE plugin hosts. */
	public enum IDE {
		/** Eclipse-IDE */
		@SerializedName("ec")
		ECLIPSE,

		/** IntelliJ-IDE */
		@SerializedName("ij")
		INTELLIJ,

		/** Android Studio (IntelliJ-IDE based) */
		@SerializedName("as")
		ANDROIDSTUDIO
	}
}
