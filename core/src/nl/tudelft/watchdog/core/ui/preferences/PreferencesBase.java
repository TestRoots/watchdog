package nl.tudelft.watchdog.core.ui.preferences;

import java.util.ArrayList;
import java.util.List;

/** Skeleton for which preferences each plugin must provide. */
public abstract class PreferencesBase {

	/** The map of registered projects. */
	protected List<ProjectPreferenceSetting> projectSettings = new ArrayList<ProjectPreferenceSetting>();

	/**
	 * Returns whether logging is enabled (<code>true</code>) or not (
	 * <code>false</code>).
	 */
    public abstract boolean isLoggingEnabled();

	/**
	 * Returns whether authentication on the url is enabled (<code>true</code>)
	 * or not ( <code>false</code>).
	 */
    public abstract boolean isAuthenticationEnabled();

    /** @return The userid. */
    public abstract String getUserId();

    public abstract void setUserId(String userid);

	/** @return the programming experience of the registered user. */
	public abstract String getProgrammingExperience();

	/** Sets the programming experience of the registered user. */
	public abstract void setProgrammingExperience(String programmingExperience);

	/** @return Whether this client version is outdated. */
    public abstract Boolean isOldVersion();

	/** Sets whether this client version is outdated. */
    public abstract void setIsOldVersion(Boolean outdated);

	/** @return Whether this client version is outdated. */
    public abstract Boolean isBigUpdateAvailable();

	/** Sets whether this client version has a big update available. */
    public abstract void setBigUpdateAvailable(Boolean available);

	/** @return Whether the user answered to the big update question. */
    public abstract Boolean isBigUpdateAnswered();

	/** Sets whether this client version has a big update available. */
    public abstract void setBigUpdateAnswered(Boolean answered);

    /** @return The number of successfully transfered intervals. */
    public abstract long getIntervals();

	/** Adds the number to the transfered intervals for the store. */
    public abstract void addTransferedIntervals(long number);

	/** @return The date the last intervals were transfered. */
    public abstract String getLastIntervalTransferDate();

	/** Sets the date when the last intervals were transfered. */
    public abstract void setLastTransferedInterval();

    /** @return The number of successfully transfered events. */
    public abstract long getEvents();

    /** Adds the number to the transfered events for the store. */
    public abstract void addTransferedEvents(long number);

    /** @return The date the last events were transfered. */
    public abstract String getLastEventTransferDate();

    /** Sets the date when the last events were transfered. */
    public abstract void setLastTransferedEvent();

	/** @return The serverURL. */
    public abstract String getServerURI();

	/**
	 * @return <code>true</code> if this project has already been registered
	 *         with WatchDog, <code>false</code> otherwise. Note: This does not
	 *         say whether WatchDog should be activated.}.
	 */
    public boolean isProjectRegistered(String project) {
    	ProjectPreferenceSetting projectSetting = getProjectSetting(
				project);
		return projectSetting != null
				&& projectSetting.startupQuestionAsked;
    }

	/**
	 * @return The matching {@link ProjectPreferenceSetting}, or a completely
	 *         new one in case there was no match.
	 */
    public ProjectPreferenceSetting getOrCreateProjectSetting(String project) {
    	ProjectPreferenceSetting setting = getProjectSetting(project);
		if (setting == null) {
			setting = new ProjectPreferenceSetting();
			setting.project = project;
			projectSettings.add(setting);
		}
		return setting;
    }

    /**
	 * @return The matching {@link ProjectPreferenceSetting}, or
	 *         <code>null</code> in case there was no match.
	 */
	private ProjectPreferenceSetting getProjectSetting(String project) {
		for (ProjectPreferenceSetting setting : projectSettings) {
			if (setting.project.equals(project)) {
				return setting;
			}
		}
		return null;
	}

	/**
	 * Registers the given project with WatchDog. If use is <code>true</code>,
	 * WatchDog will be used.
	 */
    public void registerProjectUse(String project, boolean use) {
		ProjectPreferenceSetting setting = getOrCreateProjectSetting(project);
		setting.enableWatchdog = use;
		setting.startupQuestionAsked = true;
		storeProjectSettings();
	}

	/** Registers the given projectId with the given project. */
    public void registerProjectId(String project, String projectId) {
		ProjectPreferenceSetting setting = getOrCreateProjectSetting(project);
		setting.projectId = projectId;
		storeProjectSettings();
	}

    /** Updates the serialized project settings in the preference store. */
    protected abstract void storeProjectSettings();

	/** @return a list of project settings. */
    public List<ProjectPreferenceSetting> getProjectSettings() {
		return projectSettings;
	}

	/**
	 * Resets certain WatchDog values to the default which are only used
	 * internally.
	 */
    public abstract void setDefaults();
}
