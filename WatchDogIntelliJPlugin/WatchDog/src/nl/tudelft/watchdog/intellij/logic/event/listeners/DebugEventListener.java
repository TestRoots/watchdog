package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerContextListener;
import com.intellij.debugger.impl.DebuggerSession;
import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Class that handles debug events and generates the appropriate instances of {@link DebugEventBase} which are then passed to the {@link DebugEventManager}.
 */
public class DebugEventListener implements DebuggerContextListener {

    /**
     * The {@link DebugEventManager} used for persisting and transferring the debug events.
     */
    private final DebugEventManager debugEventManager;

    /**
     * Flag used to only consider pause events after the debugging session is just started or after a resume event.
     * In this way, the pause events following step events are not considered.
     */
    private boolean firstPauseAfterStartOrResume = true;

    /**
     * Constructor.
     */
    public DebugEventListener(DebugEventManager debugEventManager) {
        this.debugEventManager = debugEventManager;
    }

    @Override
    public void changeEvent(@NotNull DebuggerContextImpl debuggerContext, DebuggerSession.Event event) {
        switch (event) {
            case ATTACHED:
                firstPauseAfterStartOrResume = true;
                break;
            case PAUSE:
                if (firstPauseAfterStartOrResume) {
                    debugEventManager.addEvent(new DebugEventBase(DebugEventType.SUSPEND_BREAKPOINT, new Date()));
                    firstPauseAfterStartOrResume = false;
                }
                break;
            case RESUME:
                debugEventManager.addEvent(new DebugEventBase(DebugEventType.RESUME_CLIENT, new Date()));
                firstPauseAfterStartOrResume = true;
                break;
        }
    }
}
