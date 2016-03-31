package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Class that handles debug events and generates the appropriate instances of {@link DebugEventBase} which are then passed to the {@link EventManager}.
 */
public class DebugEventListener implements DebuggerContextListener {

    /**
     * The {@link EventManager} used for persisting and transferring the debug events.
     */
    private final EventManager eventManager;

    /**
     * Flag used to only consider pause events after the debugging session is just started or after a resume event.
     * In this way, the pause events following step events are not considered.
     */
    private boolean firstPauseAfterStartOrResume = true;

    /**
     * Constructor.
     */
    public DebugEventListener(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl debuggerContext, DebuggerSession.Event event) {
        switch (event) {
            case ATTACHED:
                firstPauseAfterStartOrResume = true;
                break;
            case PAUSE:
                if (firstPauseAfterStartOrResume) {
                    eventManager.addEvent(new DebugEventBase(EventType.SUSPEND_BREAKPOINT, new Date()));
                    firstPauseAfterStartOrResume = false;
                }
                break;
            case RESUME:
                eventManager.addEvent(new DebugEventBase(EventType.RESUME_CLIENT, new Date()));
                firstPauseAfterStartOrResume = true;
                break;
        }
    }
}
