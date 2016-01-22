package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeClassifier;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointRemoveEvent;
import nl.tudelft.watchdog.eclipse.logic.breakpoint.BreakpointCreator;

/**
 * Listener that is notified when breakpoints are added, changed or removed.
 * Based on these notifications an instance of a subclass of
 * {@link BreakpointEventBase} is generated and given to the
 * {@link EventManager}.
 */
public class BreakpointListener implements IBreakpointListener {

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
		this.breakpoints = new HashMap<>();
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		Date timestamp = new Date();
		Breakpoint bp = BreakpointCreator.createBreakpoint(breakpoint);
		breakpoints.put(bp.getHash(), bp);
		BreakpointAddEvent event = new BreakpointAddEvent(bp.getHash(),
				bp.getBreakpointType(), timestamp);
		eventManager.addEvent(event);
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		Date timestamp = new Date();
		Breakpoint bp = BreakpointCreator.createBreakpoint(breakpoint);
		breakpoints.remove(bp.getHash());
		BreakpointRemoveEvent event = new BreakpointRemoveEvent(bp.getHash(),
				bp.getBreakpointType(), timestamp);
		eventManager.addEvent(event);
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		Date timestamp = new Date();
		Breakpoint bp = BreakpointCreator.createBreakpoint(breakpoint);

		// Replace entry if present, otherwise create new entry.
		Breakpoint old = breakpoints.put(bp.getHash(), bp);

		// TODO: multiple changes at the same time?
		BreakpointChangeType change = BreakpointChangeClassifier.classify(old,
				bp);
		BreakpointChangeEvent event = new BreakpointChangeEvent(bp.getHash(),
				bp.getBreakpointType(), change, timestamp);
		eventManager.addEvent(event);
	}

}
