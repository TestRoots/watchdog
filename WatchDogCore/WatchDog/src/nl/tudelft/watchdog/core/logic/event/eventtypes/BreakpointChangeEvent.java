package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;

/** A breakpoint change event, i.e. the user changes a breakpoint. */
public class BreakpointChangeEvent extends BreakpointEventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public BreakpointChangeEvent(Breakpoint bp, Date timestamp) {
		super(bp, EventType.BREAKPOINT_CHANGE, timestamp);
	}

}
