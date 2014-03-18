package nl.tudelft.watchdog.ui.preferences;

import nl.tudelft.watchdog.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Utils for accessing WatchDog's Eclipse preferences.
 */
public class WatchdogPreferences {
	public final static String TIMEOUT_TYPING = "TIMEOUT_EDITING";
	public final static String TIMEOUT_READING = "TIMEOUT_READING";

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
		store.setDefault(TIMEOUT_TYPING, 3000);
		store.setDefault(TIMEOUT_READING, 5000);
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

	// TODO (MMB) this is a clone from getTypingTimeout. But we are going to
	// remove both methods, so no need to consolidate.
	/** Returns the editing timeout. */
	public int getTypingTimeout() {
		int timeoutInMiliseconds = store.getInt(TIMEOUT_TYPING);
		if (timeoutInMiliseconds < 1) {
			timeoutInMiliseconds = Activator.getDefault().getPreferenceStore()
					.getDefaultInt(TIMEOUT_TYPING);
		}
		return timeoutInMiliseconds;
	}

	// TODO (MMB) this is a clone from getTypingTimeout. But we are going to
	// remove both methods, so no need to consolidate.
	/** Returns the reading timeout. */
	public int getTimeOutReading() {
		int timeout = store.getInt(TIMEOUT_READING);
		if (timeout < 1) {
			timeout = Activator.getDefault().getPreferenceStore()
					.getDefaultInt(TIMEOUT_READING);
		}
		return timeout;
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
