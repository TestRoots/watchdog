package nl.tudelft.watchdog.eclipse.logic.event.listeners;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.eclipse.logic.breakpoint.BreakpointClassifier;

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
		Breakpoint bp = createBreakpointRepresentation(breakpoint);
		breakpoints.put(bp.getHash(), bp);
		System.out.println(
				"BP added: " + bp.getBreakpointType() + " " + bp.getHash());
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		Breakpoint bp = createBreakpointRepresentation(breakpoint);
		breakpoints.remove(bp.getHash());
		System.out.println(
				"BP removed: " + bp.getBreakpointType() + " " + bp.getHash());
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		Breakpoint bp = createBreakpointRepresentation(breakpoint);
		Breakpoint old = breakpoints.put(bp.getHash(), bp); // replace entry if
															// present,
															// otherwise add
		BreakpointChangeType change = determineBreakpointChangeType(old, bp);

		System.out.println("BP changed: " + change + " "
				+ bp.getBreakpointType() + " " + bp.getHash());

	}

	private BreakpointChangeType determineBreakpointChangeType(
			Breakpoint old_bp, Breakpoint new_bp) {
		if (old_bp == null) {
			// old BP added in previous session, so unknown change
			return BreakpointChangeType.UNKNOWN;
		}

		if (old_bp.isEnabled() != new_bp.isEnabled()) {
			if (new_bp.isEnabled()) {
				return BreakpointChangeType.ENABLED;
			} else {
				return BreakpointChangeType.DISABLED;
			}
		}
		return BreakpointChangeType.UNKNOWN;
	}

	private Breakpoint createBreakpointRepresentation(IBreakpoint breakpoint) {
		Breakpoint res = new Breakpoint(
				BreakpointClassifier.classify(breakpoint),
				breakpoint.hashCode());

		try {
			res.setEnabled(breakpoint.isEnabled());
		} catch (CoreException exception) {
			exception.printStackTrace();
		}
		return res;
	}

}
