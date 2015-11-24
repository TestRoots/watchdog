package nl.tudelft.watchdog.core.ui.preferences;

import java.util.List;

public interface  PreferencesBase {
    boolean isLoggingEnabled();

    boolean isAuthenticationEnabled();

    String getUserId();

    void setUserid(String userid);

    Boolean isOldVersion();

    void setIsOldVersion(Boolean outdated);

    Boolean isBigUpdateAvailable();

    void setBigUpdateAvailable(Boolean available);

    Boolean isBigUpdateAnswered();

    void setBigUpdateAnswered(Boolean answered);

    long getIntervals();

    void addTransferedIntervals(long number);

    String getLastIntervalTransferDate();

    void setLastTransferedInterval();

    String getServerURI();

    boolean isProjectRegistered(String project);

    ProjectPreferenceSetting getOrCreateProjectSetting(String project);

    void registerProjectUse(String project, boolean use);

    void registerProjectId(String project, String projectId);

    List<ProjectPreferenceSetting> getProjectSettings();

    void setDefaults();
}
