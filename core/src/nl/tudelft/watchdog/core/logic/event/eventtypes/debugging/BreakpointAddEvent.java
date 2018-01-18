package nl.tudelft.watchdog.core.logic.event.eventtypes.debugging;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

/** A breakpoint addition event, i.e. the user adds a breakpoint. */
public class BreakpointAddEvent extends BreakpointEventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public BreakpointAddEvent(int hash, BreakpointType type, Date timestamp) {
		super(hash, type, TrackingEventType.BREAKPOINT_ADD, timestamp);
	}

}
