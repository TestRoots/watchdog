package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.breakpoint.Breakpoint;

public abstract class BreakpointEventBase extends EventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** The breakpoint associated with this {@link BreakpointEventBase}. */
	@SerializedName("bp")
	private Breakpoint breakpoint;

	/** Constructor. */
	public BreakpointEventBase(Breakpoint bp, EventType type, Date timestamp) {
		super(type, timestamp);
		this.breakpoint = bp;
	}

}
