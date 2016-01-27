package nl.tudelft.watchdog.intellij.logic.event.listeners;

import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeClassifier;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.event.EventManager;
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
        Date timestamp = new Date();
        Breakpoint bp = BreakpointCreator.createBreakpoint(xBreakpoint);
        breakpoints.put(bp.getHash(), bp);
        BreakpointAddEvent event = new BreakpointAddEvent(bp.getHash(),
                bp.getBreakpointType(), timestamp);
        eventManager.addEvent(event);
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint xBreakpoint) {
        Date timestamp = new Date();
        Breakpoint bp = BreakpointCreator.createBreakpoint(xBreakpoint);
        breakpoints.remove(bp.getHash());
        BreakpointRemoveEvent event = new BreakpointRemoveEvent(bp.getHash(),
                bp.getBreakpointType(), timestamp);
        eventManager.addEvent(event);
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint xBreakpoint) {
        Date timestamp = new Date();
        Breakpoint bp = BreakpointCreator.createBreakpoint(xBreakpoint);

        // Replace entry if present, otherwise create new entry.
        Breakpoint old = breakpoints.put(bp.getHash(), bp);

        List<BreakpointChangeType> changes = BreakpointChangeClassifier
                .classify(old, bp);
        BreakpointChangeEvent event = new BreakpointChangeEvent(bp.getHash(),
                bp.getBreakpointType(), changes, timestamp);
        eventManager.addEvent(event);
    }
}
