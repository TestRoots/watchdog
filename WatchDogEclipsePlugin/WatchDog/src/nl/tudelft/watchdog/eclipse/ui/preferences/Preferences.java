package nl.tudelft.watchdog.eclipse.ui.preferences;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Utilities for accessing WatchDog's Eclipse preferences. In the case of
 * Eclipse, a project actually corresponds to the open workspace.
 */
public class Preferences extends PreferencesBase {

	/** The user's id on the WatchDog server. */
	public final static String USERID_KEY = "USERID";

	/** The user's programming experience entered in the registration wizard. */
	public final static String PROG_EXP_KEY = "WATCHDOG.PROG_EXP";

	/** The URL of the WatchDog server. */
	public final static String SERVER_KEY = "SERVERURL";

	/** The number of successfully transfered intervals. */
	public final static String TRANSFERED_INTERVALS_KEY = "TRANSFERED_INTERVALS";

	/** The last date of successfully transfered intervals. */
	public final static String LAST_TRANSFERED_INTERVALS_KEY = "LAST_TRANSFERED_INTERVALS";

	/** The number of successfully transfered events. */
	public final static String TRANSFERED_EVENTS_KEY = "TRANSFERED_EVENTS";

	/** The last date of successfully transfered events. */
	public final static String LAST_TRANSFERED_EVENTS_KEY = "LAST_TRANSFERED_EVENTS";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED_KEY = "ENABLE_LOGGING";

	/** Flag denoting whether the WatchDog plugin is outdated. */
	public final static String IS_OLD_VERSION = "OLD_VERSION";

	/** Flag denoting whether there's a big update for WatchDog. */
	public final static String IS_BIG_UPDATE_AVAILABLE = "BIG_UPDATE";

	/** Flag denoting whether the user already answer the update question. */
	public final static String IS_BIG_UPDATE_ANSWERED = "BIG_UPDATE_ANSWERED";

	/**
	 * Flag denoting whether WatchDog plugin should do authentication or not.
	 */
	public final static String AUTHENTICATION_ENABLED_KEY = "ENABLE_AUTH";

	/** A serialized List of {@link ProjectPreferenceSetting}s. */
	public final static String WORKSPACES_KEY = "WORKSPACE_SETTINGS";

	/** The type of a list of {@link ProjectPreferenceSetting}s for Gson. */
	private final static Type TYPE_WORKSPACE_SETTINGS = new TypeToken<List<ProjectPreferenceSetting>>() {
		// intentionally empty class
	}.getType();

	/** The Gson object. */
	private final static Gson GSON = new Gson();

	/** The preference store. */
	private final ScopedPreferenceStore store;

	/** The WatchDog preference instance. */
	private static volatile Preferences singletonInstance;

	/**
	 * Constructor internally implements a singleton, not visible to class
	 * users. The preferences are stored on a per eclipse installation basis.
	 */
	private Preferences() {
		store = (ScopedPreferenceStore) Activator.getDefault()
				.getPreferenceStore();
		store.setDefault(LOGGING_ENABLED_KEY, false);
		store.setDefault(AUTHENTICATION_ENABLED_KEY, true);
		store.setDefault(USERID_KEY, "");
		store.setDefault(PROG_EXP_KEY, "");
		store.setDefault(SERVER_KEY, WatchDogGlobals.DEFAULT_SERVER_URI);
		store.setDefault(WORKSPACES_KEY, "");
		store.setDefault(TRANSFERED_INTERVALS_KEY, 0);
		store.setDefault(LAST_TRANSFERED_INTERVALS_KEY, "never");
		store.setDefault(TRANSFERED_EVENTS_KEY, 0);
		store.setDefault(LAST_TRANSFERED_EVENTS_KEY, "never");
		store.setDefault(IS_OLD_VERSION, false);
		store.setDefault(IS_BIG_UPDATE_ANSWERED, false);
		store.setDefault(IS_BIG_UPDATE_AVAILABLE, false);

		projectSettings = readSerializedWorkspaceSettings(WORKSPACES_KEY);
	}

	/**
	 * Reads and constructs a HashMap object from a serialized String preference
	 * key.
	 */
	private List<ProjectPreferenceSetting> readSerializedWorkspaceSettings(
			String key) {
		String serializedWorksapceSettings = store.getString(key);
		if (WatchDogUtils.isEmpty(serializedWorksapceSettings)) {
			return new ArrayList<ProjectPreferenceSetting>();
		}

		return GSON.fromJson(serializedWorksapceSettings,
				TYPE_WORKSPACE_SETTINGS);
	}

	/** Returns the singleton instance from WatchdogPreferences. */
	public static Preferences getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new Preferences();
		}

		return singletonInstance;
	}

	public boolean isLoggingEnabled() {
		return store.getBoolean(LOGGING_ENABLED_KEY);
	}

	public boolean isAuthenticationEnabled() {
		return store.getBoolean(AUTHENTICATION_ENABLED_KEY);
	}

	public String getUserId() {
		return store.getString(USERID_KEY);
	}

	public void setUserId(String userId) {
		store.setValue(USERID_KEY, userId);
	}

	public String getProgrammingExperience() {
		return store.getString(PROG_EXP_KEY);
	}

	public void setProgrammingExperience(String programmingExperience) {
		store.setValue(PROG_EXP_KEY, programmingExperience);
	}

	public Boolean isOldVersion() {
		return store.getBoolean(IS_OLD_VERSION);
	}

	public void setIsOldVersion(Boolean outdated) {
		store.setValue(IS_OLD_VERSION, outdated);
	}

	public Boolean isBigUpdateAvailable() {
		return store.getBoolean(IS_BIG_UPDATE_AVAILABLE);
	}

	public void setBigUpdateAvailable(Boolean available) {
		store.setValue(IS_BIG_UPDATE_AVAILABLE, available);
	}

	public Boolean isBigUpdateAnswered() {
		return store.getBoolean(IS_BIG_UPDATE_ANSWERED);
	}

	public void setBigUpdateAnswered(Boolean answered) {
		store.setValue(IS_BIG_UPDATE_ANSWERED, answered);
	}

	public long getIntervals() {
		return store.getLong(TRANSFERED_INTERVALS_KEY);
	}

	public void addTransferedIntervals(long number) {
		store.setValue(TRANSFERED_INTERVALS_KEY, getIntervals() + number);
	}

	public String getLastIntervalTransferDate() {
		return store.getString(LAST_TRANSFERED_INTERVALS_KEY);
	}

	public void setLastTransferedInterval() {
		store.setValue(LAST_TRANSFERED_INTERVALS_KEY, new Date().toString());
	}

	public long getEvents() {
		return store.getLong(TRANSFERED_EVENTS_KEY);
	}

	public void addTransferedEvents(long number) {
		store.setValue(TRANSFERED_EVENTS_KEY, getEvents() + number);
	}

	public String getLastEventTransferDate() {
		return store.getString(LAST_TRANSFERED_EVENTS_KEY);
	}

	public void setLastTransferedEvent() {
		store.setValue(LAST_TRANSFERED_EVENTS_KEY, new Date().toString());
	}

	public String getServerURI() {
		return store.getString(SERVER_KEY);
	}

	/** Updates the serialized workspace settings in the preference store. */
	protected void storeProjectSettings() {
		store.setValue(WORKSPACES_KEY,
				GSON.toJson(projectSettings, TYPE_WORKSPACE_SETTINGS));
		try {
			store.save();
		} catch (IOException exception) {
			// If this happens, our plugin is basically not functional in this
			// client setup!
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}

	/** @return The {@link IPreferenceStore} for WatchDog. */
	public IPreferenceStore getStore() {
		return store;
	}

	public void setDefaults() {
		store.setValue(WORKSPACES_KEY, "");
		store.setValue(TRANSFERED_INTERVALS_KEY, 0);
		store.setValue(LAST_TRANSFERED_INTERVALS_KEY, "never");
		store.setValue(TRANSFERED_EVENTS_KEY, 0);
		store.setValue(LAST_TRANSFERED_EVENTS_KEY, "never");
		store.setValue(IS_OLD_VERSION, false);
		store.setValue(IS_BIG_UPDATE_ANSWERED, false);
		store.setValue(IS_BIG_UPDATE_AVAILABLE, false);
	}
}
