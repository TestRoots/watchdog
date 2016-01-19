package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;

/** A breakpoint addition event, i.e. the user adds a breakpoint. */
public class BreakpointAddEvent extends BreakpointEventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public BreakpointAddEvent(Breakpoint breakpoint, Date timestamp) {
		super(breakpoint, EventType.BREAKPOINT_ADD, timestamp);
	}

}
