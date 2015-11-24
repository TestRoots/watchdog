package nl.tudelft.watchdog.core.ui.preferences;

import java.util.List;

/** Skeleton for which preferences each plugin must provide. */
public interface PreferencesBase {
	
	/**
	 * Returns whether logging is enabled (<code>true</code>) or not (
	 * <code>false</code>).
	 */
    boolean isLoggingEnabled();

	/**
	 * Returns whether authentication on the url is enabled (<code>true</code>)
	 * or not ( <code>false</code>).
	 */
    boolean isAuthenticationEnabled();

    /** @return The userid. */
    String getUserId();

    void setUserId(String userid);

	/** @return Whether this client version is outdated. */
    Boolean isOldVersion();

	/** Sets whether this client version is outdated. */
    void setIsOldVersion(Boolean outdated);

	/** @return Whether this client version is outdated. */
    Boolean isBigUpdateAvailable();

	/** Sets whether this client version has a big update available. */
    void setBigUpdateAvailable(Boolean available);

	/** @return Whether the user answered to the big update question. */
    Boolean isBigUpdateAnswered();

	/** Sets whether this client version has a big update available. */
    void setBigUpdateAnswered(Boolean answered);

    long getIntervals();

	/** Adds the number to the transfered intervals for the store. */
    void addTransferedIntervals(long number);

	/** @return The number of successfully transfered intervals. */
    String getLastIntervalTransferDate();

	/** Adds the number to the transfered intervals for the store. */
    void setLastTransferedInterval();

	/** @return The serverURL. */
    String getServerURI();

	/**
	 * @return <code>true</code> if this workspace has already been registered
	 *         with WatchDog, <code>false</code> otherwise. Note: This does not
	 *         say whether WatchDog should be activated.}.
	 */
    boolean isProjectRegistered(String project);

	/**
	 * @return The matching {@link ProjectPreferenceSetting}, or a completely
	 *         new one in case there was no match.
	 */
    ProjectPreferenceSetting getOrCreateProjectSetting(String project);

	/**
	 * Registers the given workspace with WatchDog. If use is <code>true</code>,
	 * WatchDog will be used.
	 */
    void registerProjectUse(String project, boolean use);

	/** Registers the given projectId with the given workspace. */
    void registerProjectId(String project, String projectId);

	/** @return a list of workspace settings. */
    List<ProjectPreferenceSetting> getProjectSettings();

	/**
	 * Resets certain WatchDog values to the default which are only used
	 * internally.
	 */
    void setDefaults();
}
