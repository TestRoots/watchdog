package nl.tudelft.watchdog.intellij.ui;

import com.intellij.openapi.util.Condition;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

/**
 * Should WatchDog Tool Window be active for this project. Possible future extension.
 */
public class WatchDogToolWindowFactoryCondition implements Condition {
    @Override
    public boolean value(Object o) {
        return Preferences.getInstance().getOrCreateProjectSetting(WatchDogUtils.getProjectName()).enableWatchdog;
    }
}
