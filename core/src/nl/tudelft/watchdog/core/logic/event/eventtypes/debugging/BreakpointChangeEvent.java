package nl.tudelft.watchdog.core.logic.event.eventtypes.debugging;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

/** A breakpoint change event, i.e. the user changes a breakpoint. */
public class BreakpointChangeEvent extends BreakpointEventBase {

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/**
	 * The type(s) of the change(s) to the breakpoint associated with this
	 * event.
	 */
	@SerializedName("ch")
	private List<BreakpointChangeType> changes;

	/** Constructor. */
	public BreakpointChangeEvent(int hash, BreakpointType type, List<BreakpointChangeType> changes, Date timestamp) {
		super(hash, type, TrackingEventType.BREAKPOINT_CHANGE, timestamp);
		this.changes = changes;
	}

}
