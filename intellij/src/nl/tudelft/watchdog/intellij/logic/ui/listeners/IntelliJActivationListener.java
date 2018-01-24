package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;

public class IntelliJActivationListener implements ApplicationActivationListener {

    public IntelliJActivationListener() {

    }

    @Override
    public void applicationActivated(IdeFrame ideFrame) {
        new WatchDogEvent(ideFrame, WatchDogEvent.EventType.ACTIVE_WINDOW).update();
    }

    @Override
    public void applicationDeactivated(IdeFrame ideFrame) {
        new WatchDogEvent(ideFrame, WatchDogEvent.EventType.INACTIVE_WINDOW).update();
    }

    @Override
    public void delayedApplicationDeactivated(IdeFrame ideFrame) {
        // Intentionally left empty.
    }
}
