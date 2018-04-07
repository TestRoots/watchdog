package nl.tudelft.watchdog.core.logic.event.eventtypes.debugging;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

public abstract class BreakpointEventBase extends EventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** The hash of the breakpoint associated with this event. */
	@SerializedName("bh")
	private int hash;

	/** The type of the breakpoint associated with this event. */
	@SerializedName("bt")
	private BreakpointType breakpointType;

	/** Constructor. */
	public BreakpointEventBase(int hash, BreakpointType bpType, TrackingEventType type, Date timestamp) {
		super(type, timestamp);
		this.hash = hash;
		this.breakpointType = bpType;
	}

}
