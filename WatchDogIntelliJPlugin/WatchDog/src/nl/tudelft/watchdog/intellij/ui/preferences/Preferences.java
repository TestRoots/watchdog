package nl.tudelft.watchdog.intellij.ui.preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;


import com.intellij.ide.util.PropertiesComponent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Utilities for accessing WatchDog's IntelliJ's preferences.
 */
public class Preferences extends PreferencesBase {

	/** The user's id on the WatchDog server. */
	public final static String USERID_KEY = "WATCHDOG.USERID";

	/** The URL of the WatchDog server. */
	public final static String SERVER_KEY = "WATCHDOG.SERVERURL";

	/** The number of successfully transfered intervals. */
	public final static String TRANSFERED_INTERVALS_KEY = "WATCHDOG.TRANSFERED_INTERVALS";

	/** The last date of successfully transfered intervals. */
	public final static String LAST_TRANSFERED_INTERVALS_KEY = "WATCHDOG.LAST_TRANSFERED_INTERVALS";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED_KEY = "WATCHDOG.ENABLE_LOGGING";

	/** Flag denoting whether the WatchDog plugin is outdated. */
	public final static String IS_OLD_VERSION = "WATCHDOG.OLD_VERSION";

	/** Flag denoting whether there's a big update for WatchDog. */
	public final static String IS_BIG_UPDATE_AVAILABLE = "WATCHDOG.BIG_UPDATE";

	/** Flag denoting whether the user already answer the update question. */
	public final static String IS_BIG_UPDATE_ANSWERED = "WATCHDOG.BIG_UPDATE_ANSWERED";

	/** Flag denoting whether WatchDog plugin should do authentication or not. */
	public final static String AUTHENTICATION_ENABLED_KEY = "WATCHDOG.ENABLE_AUTH";

	/** A serialized List of {@link ProjectPreferenceSetting}s. */
	public final static String WORKSPACES_KEY = "WATCHDOG.WORKSPACE_SETTINGS";

	/** The type of a list of {@link ProjectPreferenceSetting}s for Gson. */
	private final static Type TYPE_WORKSPACE_SETTINGS = new TypeToken<List<ProjectPreferenceSetting>>() {
		// intentionally empty class
	}.getType();

	/** The Gson object. */
	private final static Gson GSON = new Gson();

	/** The WatchDog preference instance. */
	private static volatile Preferences singletonInstance;

	/** The component for storing properties. */
	private final PropertiesComponent properties;

	/**
	 * Constructor internally implements a singleton, not visible to class
	 * users.
	 */
	private Preferences() {
		properties = PropertiesComponent.getInstance();
        properties.getOrInit(AUTHENTICATION_ENABLED_KEY, "true");
        properties.getOrInit(SERVER_KEY, WatchDogGlobals.DEFAULT_SERVER_URI);
        properties.getOrInit(LOGGING_ENABLED_KEY, "false");
        properties.getOrInit(TRANSFERED_INTERVALS_KEY, "0");
        properties.getOrInit(LAST_TRANSFERED_INTERVALS_KEY, "never");
        properties.getOrInit(IS_OLD_VERSION, "false");
        properties.getOrInit(IS_BIG_UPDATE_ANSWERED, "false");
        properties.getOrInit(IS_BIG_UPDATE_AVAILABLE, "false");
        properties.getOrInit(USERID_KEY, "");
		properties.getOrInit(WORKSPACES_KEY, "");
		projectSettings = readSerializedProjectSettings(WORKSPACES_KEY);
	}

	/**
	 * Reads and constructs a HashMap object from a serialized String preference
	 * key.
	 */
	private List<ProjectPreferenceSetting> readSerializedProjectSettings(
			String key) {
		String serializedWorksapceSettings = properties.getValue(key);
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

	/**
	 * Returns whether logging is enabled (<code>true</code>) or not (
	 * <code>false</code>).
	 */
	@Override
    public boolean isLoggingEnabled() {
		return properties.getBoolean(LOGGING_ENABLED_KEY, false);
	}

    public void setLoggingEnabled(boolean enable) {
        properties.setValue(LOGGING_ENABLED_KEY, String.valueOf(enable));
    }

	/**
	 * Returns whether authentication on the url is enabled (<code>true</code>)
	 * or not ( <code>false</code>).
	 */
	@Override
    public boolean isAuthenticationEnabled() {
		return properties.getBoolean(AUTHENTICATION_ENABLED_KEY, true);
	}

    public void setAuthenticationEnabled(boolean enable) {
        properties.setValue(AUTHENTICATION_ENABLED_KEY, String.valueOf(enable));
    }

	/** @return The userid. */
	@Override
    public String getUserId() {
		return properties.getValue(USERID_KEY);
	}

	/** Sets the userid for the store. */
	@Override
    public void setUserId(String userId) {
		properties.setValue(USERID_KEY, userId);
	}

	/** @return Whether this client version is outdated. */
	@Override
    public Boolean isOldVersion() {
		return properties.getBoolean(IS_OLD_VERSION, false);
	}

	/** Sets whether this client version is outdated. */
	@Override
    public void setIsOldVersion(Boolean outdated) {
		properties.setValue(IS_OLD_VERSION, outdated.toString());
	}

	/** @return Whether this client version is outdated. */
	@Override
    public Boolean isBigUpdateAvailable() {
		return properties.getBoolean(IS_BIG_UPDATE_AVAILABLE, false);
	}

	/** Sets whether this client version has a big update available. */
	@Override
    public void setBigUpdateAvailable(Boolean available) {
		properties.setValue(IS_BIG_UPDATE_AVAILABLE, available.toString());
	}

	/** @return Whether the user answered to the big update question. */
	@Override
    public Boolean isBigUpdateAnswered() {
		return properties.getBoolean(IS_BIG_UPDATE_ANSWERED, false);
	}

	/** Sets whether this client version has a big update available. */
	@Override
    public void setBigUpdateAnswered(Boolean answered) {
		properties.setValue(IS_BIG_UPDATE_ANSWERED, answered.toString());
	}

	/** @return The number of successfully transfered intervals. */
	@Override
    public long getIntervals() {
		return properties.getOrInitLong(TRANSFERED_INTERVALS_KEY, 0);
	}

	/** Adds the number to the transfered intervals for the store. */
	@Override
    public void addTransferedIntervals(long number) {
		properties.setValue(TRANSFERED_INTERVALS_KEY, Long.toString(getIntervals() + number));
	}

	/** @return The number of successfully transfered intervals. */
	@Override
    public String getLastIntervalTransferDate() {
		return properties.getValue(LAST_TRANSFERED_INTERVALS_KEY);
	}

	/** Adds the number to the transfered intervals for the store. */
	@Override
    public void setLastTransferedInterval() {
		properties.setValue(LAST_TRANSFERED_INTERVALS_KEY, new Date().toString());
	}

	/** @return The serverURL. */
	@Override
    public String getServerURI() {
		return properties.getValue(SERVER_KEY);
	}

    public void setServerURI(String url) {
        properties.setValue(SERVER_KEY, url);
    }

	/** Updates the serialized project settings in the preference store. */
	@Override
	protected void storeProjectSettings() {
		properties.setValue(WORKSPACES_KEY,
				GSON.toJson(projectSettings, TYPE_WORKSPACE_SETTINGS));
	}

	/**
	 * Resets certain WatchDog values to the default which are only used
	 * internally.
	 */
	@Override
    public void setDefaults() {
        properties.setValue(AUTHENTICATION_ENABLED_KEY, "true");
        properties.setValue(SERVER_KEY, WatchDogGlobals.DEFAULT_SERVER_URI);
        properties.setValue(LOGGING_ENABLED_KEY, "false");
		properties.setValue(TRANSFERED_INTERVALS_KEY, "0");
		properties.setValue(LAST_TRANSFERED_INTERVALS_KEY, "never");
		properties.setValue(IS_OLD_VERSION, "false");
		properties.setValue(IS_BIG_UPDATE_ANSWERED, "false");
		properties.setValue(IS_BIG_UPDATE_AVAILABLE, "false");
        properties.getOrInit(USERID_KEY, "");
        properties.getOrInit(WORKSPACES_KEY, "");
	}
}
