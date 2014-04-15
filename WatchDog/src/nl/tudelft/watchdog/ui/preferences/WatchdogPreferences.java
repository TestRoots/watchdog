package nl.tudelft.watchdog.ui.preferences;

import java.util.HashMap;

import nl.tudelft.watchdog.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.Gson;

/**
 * Utilities for accessing WatchDog's Eclipse preferences.
 */
public class WatchdogPreferences {

	/** The user's id on the WatchDog server. */
	public final static String USERID_KEY = "USERID";

	/** Flag denoting whether WatchDog plugin should do logging or not. */
	public final static String LOGGING_ENABLED_KEY = "ENABLE_LOGGING";

	/**
	 * A Hashmap from the Workspace location to a boolean flag denoting whether
	 * WatchDog is enabled for this workspace.
	 */
	public final static String ENABLED_WORKSPACES_KEY = "USE_WORKSPACES";

	/**
	 * A Hashmap from the Workspace location to the Project ID this workspaces
	 * is connected with.
	 */
	public final static String PROJECT_WORKSPACES_KEY = "PROJECT_WORKSPACES";

	/** The preference store. */
	private IPreferenceStore store;

	/** The map of registered workspaces. */
	private HashMap<String, Boolean> workspaceToActivateWatchdog = new HashMap<String, Boolean>();

	/** The map of workspaces to which project ID that workspace uses. */
	private HashMap<String, String> workspaceToProjectID = new HashMap<String, String>();

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
		store.setDefault(ENABLED_WORKSPACES_KEY, "");

		workspaceToActivateWatchdog = readSerializedHashMap(ENABLED_WORKSPACES_KEY);
		workspaceToProjectID = readSerializedHashMap(PROJECT_WORKSPACES_KEY);
	}

	/**
	 * Reads and constructs a HashMap object from a serialized String preference
	 * key.
	 */
	@SuppressWarnings("rawtypes")
	private HashMap readSerializedHashMap(String KEY) {
		String serializedWorksapceMap = store.getString(KEY);
		if (serializedWorksapceMap == null || serializedWorksapceMap.isEmpty()) {
			return new HashMap();
		}
		Gson gson = new Gson();
		return gson.fromJson(serializedWorksapceMap, HashMap.class);
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
		return (workspaceToActivateWatchdog.get(workspace) == null) ? false
				: true;
	}

	/**
	 * @return <code>true</code> if WatchDog should be actively recording for
	 *         the given workspace. <code>false</code> otherwise.
	 */
	public boolean shouldWatchDogBeActive(String workspace) {
		return workspaceToActivateWatchdog.get(workspace);
	}

	/**
	 * Registers the given workspace with WatchDog. If use is <code>true</code>,
	 * WatchDog will be used.
	 */
	public void registerWorkspaceUse(String workspace, boolean use) {
		storeHashMapPreferenceValue(workspaceToActivateWatchdog,
				ENABLED_WORKSPACES_KEY, workspace, use);
	}

	/** Registers the given projectId with the given workspace. */
	public void registerWorkspaceProject(String workspace, String projectId) {
		storeHashMapPreferenceValue(workspaceToProjectID,
				PROJECT_WORKSPACES_KEY, workspace, projectId);
	}

	/**
	 * Updates a serialized HashMap in the preference store with the supplied
	 * values.
	 * 
	 * @param map
	 *            The HashMap that is to be updated.
	 * @param preferenceKey
	 *            The Key in the preferences under which the Map is stored in a
	 *            serialized manner.
	 * @param hashmapKey
	 *            The key to put into the map.
	 * @param value
	 *            The value to put into the map.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void storeHashMapPreferenceValue(HashMap map, String preferenceKey,
			String hashmapKey, Object value) {
		map.put(hashmapKey, value);
		Gson gson = new Gson();
		String serializedObject = gson.toJson(map);
		store.setValue(preferenceKey, serializedObject);
	}

	/** @return The {@link IPreferenceStore} for WatchDog. */
	public IPreferenceStore getStore() {
		return store;
	}

}
