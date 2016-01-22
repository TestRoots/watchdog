package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;

/** A breakpoint change event, i.e. the user changes a breakpoint. */
public class BreakpointChangeEvent extends BreakpointEventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;
	
	/** The type of the change of the breakpoint associated with this event. */
	@SerializedName("ct")
	private BreakpointChangeType changeType;

	/** Constructor. */
	public BreakpointChangeEvent(int hash, BreakpointType type, BreakpointChangeType change, Date timestamp) {
		super(hash, type, EventType.BREAKPOINT_CHANGE, timestamp);
		this.changeType = change;
	}

}
