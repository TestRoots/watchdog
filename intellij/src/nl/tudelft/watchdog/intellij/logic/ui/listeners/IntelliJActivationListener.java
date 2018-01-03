package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;

public class IntelliJActivationListener implements ApplicationActivationListener {

    private final WatchDogEventManager eventManager;

    public IntelliJActivationListener(WatchDogEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void applicationActivated(IdeFrame ideFrame) {
        eventManager.update(new WatchDogEvent(ideFrame, WatchDogEvent.EventType.ACTIVE_WINDOW));
    }

    @Override
    public void applicationDeactivated(IdeFrame ideFrame) {
        eventManager.update(new WatchDogEvent(ideFrame, WatchDogEvent.EventType.INACTIVE_WINDOW));
    }

    @Override
    public void delayedApplicationDeactivated(IdeFrame ideFrame) {
        // Intentionally left empty.
    }
}
