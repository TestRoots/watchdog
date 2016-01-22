package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeClassifier;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointEventBase;
import nl.tudelft.watchdog.eclipse.logic.breakpoint.BreakpointCreator;

/**
 * Listener that is notified when breakpoints are added, changed or removed.
 * Based on these notifications an instance of a subclass of
 * {@link BreakpointEventBase} is generated and given to the
 * {@link EventManager}.
 */
public class BreakpointListener implements IBreakpointListener {

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
		Breakpoint bp = BreakpointCreator.createBreakpoint(breakpoint);
		breakpoints.put(bp.getHash(), bp);
		System.out.println(
				"BP added: " + bp.getBreakpointType() + " " + bp.getHash());
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		Breakpoint bp = BreakpointCreator.createBreakpoint(breakpoint);
		breakpoints.remove(bp.getHash());
		System.out.println(
				"BP removed: " + bp.getBreakpointType() + " " + bp.getHash());
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		Breakpoint bp = BreakpointCreator.createBreakpoint(breakpoint);
		Breakpoint old = breakpoints.put(bp.getHash(), bp); // replace entry if
															// present,
															// otherwise add
		BreakpointChangeType change = BreakpointChangeClassifier.classify(old,
				bp);
		// TODO: multiple changes at the same time?

		System.out.println("BP changed: " + change + " "
				+ bp.getBreakpointType() + " " + bp.getHash());

	}

}
