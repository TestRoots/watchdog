package nl.tudelft.watchdog.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.tudelft.watchdog.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.Gson;

/**
 * Utilities for accessing WatchDog's Eclipse preferences.
 */
public class Preferences {

	/** The user's id on the WatchDog server. */
	public final static String USERID_KEY = "USERID";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED_KEY = "ENABLE_LOGGING";

	/**
	 * A serialized List of {@link WorkspacePreferenceSetting}s.
	 */
	public final static String WORKSPACES_KEY = "WORKSPACE_SETTINGS";

	/** The preference store. */
	private IPreferenceStore store;

	/** The map of registered workspaces. */
	private List<WorkspacePreferenceSetting> workspaceSettings = new ArrayList<WorkspacePreferenceSetting>();

	/** The WatchDog preference instance. */
	private static Preferences singletonInstance;

	/**
	 * Constructor internally implements a singleton, not visible to class
	 * users.
	 */
	private Preferences() {
		store = Activator.getDefault().getPreferenceStore();
		store.setDefault(LOGGING_ENABLED_KEY, false);
		store.setDefault(USERID_KEY, "");
		store.setDefault(WORKSPACES_KEY, "");

		workspaceSettings = readSerializedWorkspaceSettings(WORKSPACES_KEY);
	}

	/**
	 * Reads and constructs a HashMap object from a serialized String preference
	 * key.
	 */
	private List<WorkspacePreferenceSetting> readSerializedWorkspaceSettings(
			String KEY) {
		String serializedWorksapceSettings = store.getString(KEY);
		if (serializedWorksapceSettings == null
				|| serializedWorksapceSettings.isEmpty()) {
			return new ArrayList<WorkspacePreferenceSetting>();
		}
		Gson gson = new Gson();
		WorkspacePreferenceSetting[] settings = gson
				.fromJson(serializedWorksapceSettings,
						WorkspacePreferenceSetting[].class);
		return Arrays.asList(settings);
	}

	/** Returns the singleton instance from WatchdogPreferences. */
	public static Preferences getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new Preferences();
		}

		return singletonInstance;
	}

	/** Returns whether logging is enabled (true) or not (false). */
	public boolean isLoggingEnabled() {
		return store.getBoolean(LOGGING_ENABLED_KEY);
	}

	/** @return The userid. */
	public String getUserid() {
		return store.getString(USERID_KEY);
	}

	/**
	 * @return <code>true</code> if this workspace has already been registered
	 *         with WatchDog, <code>false</code> otherwise. Note: This does not
	 *         say whether WatchDog should be activated, which is returned by
	 *         {@link #shouldWatchDogBeActive(String)}.
	 */
	public boolean isWorkspaceRegistered(String workspace) {
		return (getWorkspaceSetting(workspace) == null) ? false : true;
	}

	/**
	 * @return <code>true</code> if WatchDog should be actively recording for
	 *         the given workspace. <code>false</code> otherwise.
	 */
	public boolean shouldWatchDogBeActive(String workspace) {
		return getWorkspaceSetting(workspace).enableWatchdog;
	}

	/**
	 * @return The matching {@link WorkspacePreferenceSetting}, or
	 *         <code>null</code> in case there was no match.
	 */
	public WorkspacePreferenceSetting getWorkspaceSetting(String workspace) {
		for (WorkspacePreferenceSetting setting : workspaceSettings) {
			if (setting.workspace.equals(workspace)) {
				return setting;
			}
		}
		return null;
	}

	/**
	 * @return The matching {@link WorkspacePreferenceSetting}, or a completely
	 *         new one in case there was no match.
	 */
	private WorkspacePreferenceSetting getOrCreateWorkspaceSetting(
			String workspace) {
		WorkspacePreferenceSetting setting = getWorkspaceSetting(workspace);
		if (setting == null) {
			setting = new WorkspacePreferenceSetting();
			workspaceSettings.add(setting);
			setting.workspace = workspace;
		}
		return setting;
	}

	/**
	 * Registers the given workspace with WatchDog. If use is <code>true</code>,
	 * WatchDog will be used.
	 */
	public void registerWorkspaceUse(String workspace, boolean use) {
		WorkspacePreferenceSetting setting = getOrCreateWorkspaceSetting(workspace);
		setting.enableWatchdog = use;
		storeWorkspaceSettings();
	}

	/** Registers the given projectId with the given workspace. */
	public void registerWorkspaceProject(String workspace, String projectId) {
		WorkspacePreferenceSetting setting = getOrCreateWorkspaceSetting(workspace);
		setting.projectId = projectId;
		storeWorkspaceSettings();
	}

	/** Updates the serialized workspace settings in the preference store. */
	private void storeWorkspaceSettings() {
		Gson gson = new Gson();
		store.setValue(WORKSPACES_KEY, gson.toJson(workspaceSettings));
	}

	/** @return The {@link IPreferenceStore} for WatchDog. */
	public IPreferenceStore getStore() {
		return store;
	}

	/**
	 * @return a list of workspace settings.
	 */
	public List<WorkspacePreferenceSetting> getWorkspaceSettings() {
		return workspaceSettings;
	}

}
