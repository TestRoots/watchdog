package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

public class IntelliJActivationListener implements ApplicationActivationListener {

    public IntelliJActivationListener() {

    }

    @Override
    public void applicationActivated(IdeFrame ideFrame) {
        WatchDogEventType.ACTIVE_WINDOW.process(ideFrame);
    }

    @Override
    public void applicationDeactivated(IdeFrame ideFrame) {
        WatchDogEventType.INACTIVE_WINDOW.process(ideFrame);
    }

    @Override
    public void delayedApplicationDeactivated(IdeFrame ideFrame) {
        // Intentionally left empty.
    }
}
