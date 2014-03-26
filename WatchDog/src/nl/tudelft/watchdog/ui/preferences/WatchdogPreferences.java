package nl.tudelft.watchdog.ui.preferences;

import nl.tudelft.watchdog.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Utils for accessing WatchDog's Eclipse preferences.
 */
public class WatchdogPreferences {

	/** The user's id on the WatchDog server. */
	public final static String USERID = "USERID";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED = "ENABLE_LOGGING";

	/** The preference store. */
	private IPreferenceStore store;

	/**
	 * The WatchDog preference instance
	 */
	private static WatchdogPreferences singletonInstance;

	/**
	 * Constructor internally implements a singleton, not visible to class
	 * users.
	 */
	private WatchdogPreferences() {
		store = Activator.getDefault().getPreferenceStore();
		store.setDefault(LOGGING_ENABLED, false);
		store.setDefault(USERID, "");
	}

	/** Returns the singleton instance from WatchdogPreferences. */
	public static WatchdogPreferences getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new WatchdogPreferences();
		}
		return singletonInstance;
	}

	/** Returns whether logging is enabled (true) or not (false). */
	public boolean isLoggingEnabled() {
		return store.getBoolean(LOGGING_ENABLED);
	}

	/** @return The userid. */
	public static String getUserid() {
		return USERID;
	}

	/** @return The {@link IPreferenceStore} for WatchDog. */
	public IPreferenceStore getStore() {
		return store;
	}

}
