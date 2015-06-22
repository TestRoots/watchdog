package nl.tudelft.watchdog.ui;

import com.intellij.openapi.util.Condition;
import nl.tudelft.watchdog.WatchDog;
import nl.tudelft.watchdog.ui.preferences.Preferences;

/**
 * Should WatchDog Tool Window be active for this project. Possible future extension.
 */
public class WatchDogToolWindowFactoryCondition implements Condition {
    @Override
    public boolean value(Object o) {
        return Preferences.getInstance().getOrCreateProjectSetting(WatchDog.project.getName()).enableWatchdog;
    }
}
