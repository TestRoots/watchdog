package nl.tudelft.watchdog.ui.preferences;

/** Contains the settings for a project in the preferences. */
public class ProjectPreferenceSetting {
	/** The project location. */
	public String project = "";

	/** The projectId. */
	public String projectId = "";

	/**
	 * Flag denoting whether Watchdog should be activated for this project (
	 * <code>true</code>), or not.
	 */
	public boolean enableWatchdog = false;

	/**
	 * Flag denoting whether Watchdog has already asked the user whether it
	 * should be active for this project.
	 */
	public boolean startupQuestionAsked = false;
}