package nl.tudelft.watchdog.preferences;

import nl.tudelft.watchdog.plugin.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A Utils class for accessing WatchDog's Eclipse preferences.
 */
public class PreferenceUtils {
	/* package */static IPreferenceStore store;

	public final static String TIMEOUT_TYPING = "TIMEOUT_EDITING";
	public final static String TIMEOUT_READING = "TIMEOUT_READING";
	public final static String DEBUGGING_ENABLED = "ENABLE_DEBUGGING";

	// TODO (MMB) these static blocks are terrible.
	static {
		store = Activator.getDefault().getPreferenceStore();
		store.setDefault(TIMEOUT_TYPING, 3000);
		store.setDefault(TIMEOUT_READING, 5000);
		store.setDefault(DEBUGGING_ENABLED, false);
	}

	/** Returns the editing timeout. */
	public static int getTypingTimeout() {
		int ms = store.getInt(TIMEOUT_TYPING);
		if (ms < 1) {
			ms = Activator.getDefault().getPreferenceStore()
					.getDefaultInt(TIMEOUT_TYPING);
		}
		return ms;
	}

	// TODO (MMB) this is a clone from getTypingTimeout. But we are going to
	// remove both methods, so no need to consolidate.
	/** Returns the reading timeout. */
	public static int getTimeOutReading() {
		int ms = store.getInt(TIMEOUT_READING);
		if (ms < 1) {
			ms = Activator.getDefault().getPreferenceStore()
					.getDefaultInt(TIMEOUT_READING);
		}
		return ms;
	}

	/**
	 * Returns whether debugging is enabled (true) or not (false).
	 */
	public static boolean isDebuggingEnabled() {
		return store.getBoolean(DEBUGGING_ENABLED);
	}

}
