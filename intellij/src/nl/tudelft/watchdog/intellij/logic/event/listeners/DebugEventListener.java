package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Class that handles debug events and generates the appropriate instances of {@link DebugEventBase} which are then passed to the {@link TrackingEventManager}.
 */
public class DebugEventListener implements DebuggerContextListener {

    /**
     * The {@link TrackingEventManager} used for persisting and transferring the debug events.
     */
    private final TrackingEventManager trackingEventManager;

    /**
     * Flag used to only consider pause events after the debugging session is just started or after a resume event.
     * In this way, the pause events following step events are not considered.
     */
    private boolean firstPauseAfterStartOrResume = true;

    /**
     * Constructor.
     */
    public DebugEventListener(TrackingEventManager trackingEventManager) {
        this.trackingEventManager = trackingEventManager;
    }

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl debuggerContext, DebuggerSession.Event event) {
        switch (event) {
            case ATTACHED:
                firstPauseAfterStartOrResume = true;
                break;
            case PAUSE:
                if (firstPauseAfterStartOrResume) {
                    trackingEventManager.addEvent(new DebugEventBase(TrackingEventType.SUSPEND_BREAKPOINT, new Date()));
                    firstPauseAfterStartOrResume = false;
                }
                break;
            case RESUME:
                trackingEventManager.addEvent(new DebugEventBase(TrackingEventType.RESUME_CLIENT, new Date()));
                firstPauseAfterStartOrResume = true;
                break;
        }
    }
}
