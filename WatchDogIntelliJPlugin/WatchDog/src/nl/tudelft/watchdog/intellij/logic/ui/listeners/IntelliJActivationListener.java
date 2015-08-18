package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import nl.tudelft.watchdog.intellij.logic.ui.EventManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;

public class IntelliJActivationListener implements ApplicationActivationListener {

    private final EventManager eventManager;

    public IntelliJActivationListener(EventManager eventManager) {
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
}
