package nl.tudelft.watchdog.ui.preferences;

/** Contains the settings for a workspace in the preferences. */
class WorkspacePreferenceSetting {
	/** The workspace location. */
	String workspace;

	/** The projectId of the workspace. */
	String projectId;

	/**
	 * Flag denoting whether Watchdog should be activated for this workspace
	 * (<code>true</code>), or not.
	 */
	boolean enableWatchdog;
}