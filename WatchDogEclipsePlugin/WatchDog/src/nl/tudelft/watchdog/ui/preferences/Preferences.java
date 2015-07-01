package nl.tudelft.watchdog.ui.preferences;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.Activator;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Utilities for accessing WatchDog's Eclipse preferences.
 */
public class Preferences implements PreferencesBase {

	/** The user's id on the WatchDog server. */
	public final static String USERID_KEY = "USERID";

	/** The URL of the WatchDog server. */
	public final static String SERVER_KEY = "SERVERURL";

	/** The number of successfully transfered intervals. */
	public final static String TRANSFERED_INTERVALS_KEY = "TRANSFERED_INTERVALS";

	/** The last date of successfully transfered intervals. */
	public final static String LAST_TRANSFERED_INTERVALS_KEY = "LAST_TRANSFERED_INTERVALS";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED_KEY = "ENABLE_LOGGING";

	/** Flag denoting whether the WatchDog plugin is outdated. */
	public final static String IS_OLD_VERSION = "OLD_VERSION";

	/** Flag denoting whether there's a big update for WatchDog. */
	public final static String IS_BIG_UPDATE_AVAILABLE = "BIG_UPDATE";

	/** Flag denoting whether the user already answer the update question. */
	public final static String IS_BIG_UPDATE_ANSWERED = "BIG_UPDATE_ANSWERED";

	/** Flag denoting whether WatchDog plugin should do authentication or not. */
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

	/** The map of registered workspaces. */
	private List<ProjectPreferenceSetting> workspaceSettings = new ArrayList<ProjectPreferenceSetting>();

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
		store.setDefault(SERVER_KEY, WatchDogGlobals.DEFAULT_SERVER_URI);
		store.setDefault(WORKSPACES_KEY, "");
		store.setDefault(TRANSFERED_INTERVALS_KEY, 0);
		store.setDefault(LAST_TRANSFERED_INTERVALS_KEY, "never");
		store.setDefault(IS_OLD_VERSION, false);
		store.setDefault(IS_BIG_UPDATE_ANSWERED, false);
		store.setDefault(IS_BIG_UPDATE_AVAILABLE, false);

		workspaceSettings = readSerializedWorkspaceSettings(WORKSPACES_KEY);
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

	/**
	 * Returns whether logging is enabled (<code>true</code>) or not (
	 * <code>false</code>).
	 */
	public boolean isLoggingEnabled() {
		return store.getBoolean(LOGGING_ENABLED_KEY);
	}

	/**
	 * Returns whether authentication on the url is enabled (<code>true</code>)
	 * or not ( <code>false</code>).
	 */
	public boolean isAuthenticationEnabled() {
		return store.getBoolean(AUTHENTICATION_ENABLED_KEY);
	}

	/** @return The userid. */
	public String getUserid() {
		return store.getString(USERID_KEY);
	}

	/** Sets the userid for the store. */
	public void setUserid(String userid) {
		store.setValue(USERID_KEY, userid);
	}

	/** @return Whether this client version is outdated. */
	public Boolean isOldVersion() {
		return store.getBoolean(IS_OLD_VERSION);
	}

	/** Sets whether this client version is outdated. */
	public void setIsOldVersion(Boolean outdated) {
		store.setValue(IS_OLD_VERSION, outdated);
	}

	/** @return Whether this client version is outdated. */
	public Boolean isBigUpdateAvailable() {
		return store.getBoolean(IS_BIG_UPDATE_AVAILABLE);
	}

	/** Sets whether this client version has a big update available. */
	public void setBigUpdateAvailable(Boolean available) {
		store.setValue(IS_BIG_UPDATE_AVAILABLE, available);
	}

	/** @return Whether the user answered to the big update question. */
	public Boolean isBigUpdateAnswered() {
		return store.getBoolean(IS_BIG_UPDATE_ANSWERED);
	}

	/** Sets whether this client version has a big update available. */
	public void setBigUpdateAnswered(Boolean answered) {
		store.setValue(IS_BIG_UPDATE_ANSWERED, answered);
	}

	/** @return The number of successfully transfered intervals. */
	public long getIntervals() {
		return store.getLong(TRANSFERED_INTERVALS_KEY);
	}

	/** Adds the number to the transfered intervals for the store. */
	public void addTransferedIntervals(long number) {
		store.setValue(TRANSFERED_INTERVALS_KEY, getIntervals() + number);
	}

	/** @return The number of successfully transfered intervals. */
	public String getLastIntervalTransferDate() {
		return store.getString(LAST_TRANSFERED_INTERVALS_KEY);
	}

	/** Adds the number to the transfered intervals for the store. */
	public void setLastTransferedInterval() {
		store.setValue(LAST_TRANSFERED_INTERVALS_KEY, new Date().toString());
	}

	/** @return The serverURL. */
	public String getServerURI() {
		return store.getString(SERVER_KEY);
	}

	/**
	 * @return <code>true</code> if this workspace has already been registered
	 *         with WatchDog, <code>false</code> otherwise. Note: This does not
	 *         say whether WatchDog should be activated, which is returned by
	 *         {@link #shouldWatchDogBeActive(String)}.
	 */
	public boolean isProjectRegistered(String workspace) {
		ProjectPreferenceSetting workspaceSetting = getProjectSetting(workspace);
		return (workspaceSetting != null && workspaceSetting.startupQuestionAsked) ? true
				: false;
	}

	/**
	 * @return The matching {@link ProjectPreferenceSetting}, or a completely
	 *         new one in case there was no match.
	 */
	public ProjectPreferenceSetting getOrCreateProjectSetting(String workspace) {
		ProjectPreferenceSetting setting = getProjectSetting(workspace);
		if (setting == null) {
			setting = new ProjectPreferenceSetting();
			setting.project = workspace;
			workspaceSettings.add(setting);
		}
		return setting;
	}

	/**
	 * @return The matching {@link ProjectPreferenceSetting}, or
	 *         <code>null</code> in case there was no match.
	 */
	private ProjectPreferenceSetting getProjectSetting(String workspace) {
		for (ProjectPreferenceSetting setting : workspaceSettings) {
			if (setting.project.equals(workspace)) {
				return setting;
			}
		}
		return null;
	}

	/**
	 * Registers the given workspace with WatchDog. If use is <code>true</code>,
	 * WatchDog will be used.
	 */
	public void registerProjectUse(String workspace, boolean use) {
		ProjectPreferenceSetting setting = getOrCreateProjectSetting(workspace);
		setting.enableWatchdog = use;
		setting.startupQuestionAsked = true;
		storeProjectSettings();
	}

	/** Registers the given projectId with the given workspace. */
	public void registerProjectId(String workspace, String projectId) {
		ProjectPreferenceSetting setting = getOrCreateProjectSetting(workspace);
		setting.projectId = projectId;
		storeProjectSettings();
	}

	/** Updates the serialized workspace settings in the preference store. */
	private void storeProjectSettings() {
		store.setValue(WORKSPACES_KEY,
				GSON.toJson(workspaceSettings, TYPE_WORKSPACE_SETTINGS));
		try {
			store.save();
		} catch (IOException exception) {
			// If this happens, our plugin is basically not functional in this
			// client setup!
			WatchDogLogger.getInstance(isLoggingEnabled()).logSevere(exception);
		}
	}

	/** @return The {@link IPreferenceStore} for WatchDog. */
	public IPreferenceStore getStore() {
		return store;
	}

	/** @return a list of workspace settings. */
	public List<ProjectPreferenceSetting> getProjectSettings() {
		return workspaceSettings;
	}

	/**
	 * Resets certain WatchDog values to the default which are only used
	 * internally.
	 */
	public void setDefaults() {
		store.setValue(WORKSPACES_KEY, "");
		store.setValue(TRANSFERED_INTERVALS_KEY, 0);
		store.setValue(LAST_TRANSFERED_INTERVALS_KEY, "never");
		store.setValue(IS_OLD_VERSION, false);
		store.setValue(IS_BIG_UPDATE_ANSWERED, false);
		store.setValue(IS_BIG_UPDATE_AVAILABLE, false);
	}
}
