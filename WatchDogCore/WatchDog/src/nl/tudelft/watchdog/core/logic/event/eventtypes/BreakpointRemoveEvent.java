package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/** A breakpoint remove event, i.e. the user removes a breakpoint. */
public class BreakpointRemoveEvent extends BreakpointEventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	public BreakpointRemoveEvent(int hash, BreakpointType type, Date timestamp) {
		super(hash, type, DebugEventType.BREAKPOINT_REMOVE, timestamp);
	}

}
