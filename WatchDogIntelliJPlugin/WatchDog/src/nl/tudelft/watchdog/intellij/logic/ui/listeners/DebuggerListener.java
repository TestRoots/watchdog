package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.intellij.logic.ui.WatchDogEventManager;

/**
 * Listener for debug events.
 */
public class DebuggerListener implements DebuggerManagerListener {

    private final WatchDogEventManager eventManager;

    /**
     * Constructor.
     */
    public DebuggerListener(WatchDogEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void sessionCreated(DebuggerSession debuggerSession) {
        eventManager.update(new WatchDogEvent(this, WatchDogEvent.EventType.START_DEBUG));
    }

    @Override
    public void sessionAttached(DebuggerSession debuggerSession) {
        // Intentionally left empty
    }

    @Override
    public void sessionDetached(DebuggerSession debuggerSession) {
        // Intentionally left empty
    }

    @Override
    public void sessionRemoved(DebuggerSession debuggerSession) {
        eventManager.update(new WatchDogEvent(this, WatchDogEvent.EventType.END_DEBUG));
    }
}
