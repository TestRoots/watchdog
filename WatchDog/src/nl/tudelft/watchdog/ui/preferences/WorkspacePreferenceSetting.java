package nl.tudelft.watchdog.ui.preferences;

/** Contains the settings for a workspace in the preferences. */
public class WorkspacePreferenceSetting {
	/** The workspace location. */
	public String workspace = "";

	/** The projectId of the workspace. */
	public String projectId = "";

	/**
	 * A pointer to the Persistence storage denoting the last transfered
	 * interval.
	 */
	public long lastTransferedInterval = 0;

	/**
	 * Flag denoting whether Watchdog should be activated for this workspace (
	 * <code>true</code>), or not.
	 */
	public boolean enableWatchdog = false;
}