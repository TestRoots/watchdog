package nl.tudelft.watchdog.intellij.logic.ui.listeners;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

/**
 * Listener for debug events.
 */
public class DebuggerListener implements DebuggerManagerListener {

    /**
     * Constructor.
     */
    public DebuggerListener() {
    }

    @Override
    public void sessionCreated(DebuggerSession debuggerSession) {
        WatchDogEventType.START_DEBUG.process(this);
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
        WatchDogEventType.END_DEBUG.process(this);
    }
}
