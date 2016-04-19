package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeClassifier;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointRemoveEvent;
import nl.tudelft.watchdog.intellij.logic.breakpoint.BreakpointCreator;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listener that is notified when breakpoints are added, changed or removed.
 * Based on these notifications an instance of a subclass of
 * {@link BreakpointEventBase} is generated and given to the
 * {@link DebugEventManager}.
 */
public class BreakpointListener implements XBreakpointListener {

    /** The event manager that should receive the generated events. */
    private final DebugEventManager debugEventManager;

    /**
     * Map containing all breakpoints added or changed (and not removed) in this
     * session indexing by their hash code.
     */
    private final Map<Integer, Breakpoint> breakpoints;

    /** Constructor. */
    public BreakpointListener(DebugEventManager debugEventManager) {
        this.debugEventManager = debugEventManager;
        this.breakpoints = new HashMap<Integer, Breakpoint>();
    }

    @Override
    public void breakpointAdded(@NotNull XBreakpoint xBreakpoint) {
        Date timestamp = new Date();
        Breakpoint breakpoint = BreakpointCreator.createBreakpoint(xBreakpoint);
        breakpoints.put(breakpoint.getHash(), breakpoint);
        BreakpointAddEvent event = new BreakpointAddEvent(breakpoint.getHash(),
                breakpoint.getBreakpointType(), timestamp);
        debugEventManager.addEvent(event);
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint xBreakpoint) {
        Date timestamp = new Date();
        Breakpoint breakpoint = BreakpointCreator.createBreakpoint(xBreakpoint);
        breakpoints.remove(breakpoint.getHash());
        BreakpointRemoveEvent event = new BreakpointRemoveEvent(breakpoint.getHash(),
                breakpoint.getBreakpointType(), timestamp);
        debugEventManager.addEvent(event);
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint xBreakpoint) {
        Date timestamp = new Date();
        Breakpoint breakpoint = BreakpointCreator.createBreakpoint(xBreakpoint);

        // Replace entry if present, otherwise create new entry.
        Breakpoint oldBreakpoint = breakpoints.put(breakpoint.getHash(), breakpoint);

        List<BreakpointChangeType> changes = BreakpointChangeClassifier
                .classify(oldBreakpoint, breakpoint);
        BreakpointChangeEvent event = new BreakpointChangeEvent(breakpoint.getHash(),
                breakpoint.getBreakpointType(), changes, timestamp);
        debugEventManager.addEvent(event);
    }
}
