package nl.tudelft.watchdog.core.ui.preferences;

import java.util.List;

public abstract class PreferencesBase {
    public abstract boolean isLoggingEnabled();

    public abstract boolean isAuthenticationEnabled();

    public abstract String getUserid();

    public abstract void setUserid(String userid);

    public abstract Boolean isOldVersion();

    public abstract void setIsOldVersion(Boolean outdated);

    public abstract Boolean isBigUpdateAvailable();

    public abstract void setBigUpdateAvailable(Boolean available);

    public abstract Boolean isBigUpdateAnswered();

    public abstract void setBigUpdateAnswered(Boolean answered);

    public abstract long getIntervals();

    public abstract void addTransferedIntervals(long number);

    public abstract String getLastIntervalTransferDate();

    public abstract void setLastTransferedInterval();

    public abstract String getServerURI();

    public abstract boolean isProjectRegistered(String project);

    public abstract ProjectPreferenceSetting getOrCreateProjectSetting(String project);

    public abstract void registerProjectUse(String project, boolean use);

    public abstract void registerProjectId(String project, String projectId);

    public abstract List<ProjectPreferenceSetting> getProjectSettings();

    public abstract void setDefaults();
}
