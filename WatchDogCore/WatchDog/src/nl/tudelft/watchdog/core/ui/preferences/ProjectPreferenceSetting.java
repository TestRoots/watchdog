package nl.tudelft.watchdog.core.ui.preferences;

/** Contains the settings for a project/workspace in the preferences. */
public class ProjectPreferenceSetting {
	/** The project/workspace location. */
	public String project = "";

	/** The projectId. */
	public String projectId = "";

	/**
	 * Flag denoting whether Watchdog should be activated for this project/workspace (
	 * <code>true</code>), or not.
	 */
	public boolean enableWatchdog = false;

	/**
	 * Flag denoting whether Watchdog has already asked the user whether it
	 * should be active for this project/workspace.
	 */
	public boolean startupQuestionAsked = false;
}