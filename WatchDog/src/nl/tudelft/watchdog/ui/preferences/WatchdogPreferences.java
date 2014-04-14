package nl.tudelft.watchdog.ui.preferences;

import java.util.HashMap;

import nl.tudelft.watchdog.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.Gson;

/**
 * Utils for accessing WatchDog's Eclipse preferences.
 */
public class WatchdogPreferences {

	/** The user's id on the WatchDog server. */
	public final static String USERID_KEY = "USERID";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED_KEY = "ENABLE_LOGGING";

	/** The workspaces in which WatchDog is enabled. */
	public final static String WORKSPACES_KEY = "WORKSPACES";

	/** The preference store. */
	private IPreferenceStore store;

	/** The map of registered workspaces. */
	private HashMap<String, Boolean> registeredWorkspacesMap = new HashMap<String, Boolean>();

	/** The WatchDog preference instance. */
	private static WatchdogPreferences singletonInstance;

	/**
	 * Constructor internally implements a singleton, not visible to class
	 * users.
	 */
	@SuppressWarnings("unchecked")
	private WatchdogPreferences() {
		store = Activator.getDefault().getPreferenceStore();
		store.setDefault(LOGGING_ENABLED_KEY, false);
		store.setDefault(USERID_KEY, "");
		store.setDefault(WORKSPACES_KEY, "");
		store.setValue(WORKSPACES_KEY, "");

		String serializedWorksapceMap = store.getString(WORKSPACES_KEY);
		if (serializedWorksapceMap == null || serializedWorksapceMap.isEmpty()) {
			return;
		}
		Gson gson = new Gson();
		registeredWorkspacesMap = gson.fromJson(serializedWorksapceMap,
				HashMap.class);
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
	 *         {@link #shouldWatchDogBeActive(String)}
	 */
	public boolean isWorkspaceRegistered(String workspace) {
		return (registeredWorkspacesMap.get(workspace) == null) ? false : true;
	}

	/**
	 * @return <code>true</code> if WatchDog should be actively recording for
	 *         the given workspace. <code>false</code> otherwise.
	 */
	public boolean shouldWatchDogBeActive(String workspace) {
		return registeredWorkspacesMap.get(workspace);
	}

	/**
	 * Registers the given workspace with WatchDog. If use is <code>true</code>,
	 * WatchDog will be used.
	 */
	public void registerWorkspace(String workspace, boolean use) {
		registeredWorkspacesMap.put(workspace, use);
		Gson gson = new Gson();
		String serializedObject = gson.toJson(registeredWorkspacesMap);
		store.setValue(WORKSPACES_KEY, serializedObject);
	}

	/** @return The {@link IPreferenceStore} for WatchDog. */
	public IPreferenceStore getStore() {
		return store;
	}

}
