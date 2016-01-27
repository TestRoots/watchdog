package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointEventBase;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Listener that is notified when breakpoints are added, changed or removed.
 * Based on these notifications an instance of a subclass of
 * {@link BreakpointEventBase} is generated and given to the
 * {@link EventManager}.
 */
public class BreakpointListener implements XBreakpointListener {

    /** The event manager that should receive the generated events. */
    private final EventManager eventManager;

    /**
     * Map containing all breakpoints added or changed (and not removed) in this
     * session indexing by their hash code.
     */
    private final Map<Integer, Breakpoint> breakpoints;

    /** Constructor. */
    public BreakpointListener(EventManager eventManager) {
        this.eventManager = eventManager;
        this.breakpoints = new HashMap<Integer, Breakpoint>();
    }

    @Override
    public void breakpointAdded(@NotNull XBreakpoint xBreakpoint) {
        //TODO
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint xBreakpoint) {
        //TODO
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint xBreakpoint) {
        //TODO
    }
}
