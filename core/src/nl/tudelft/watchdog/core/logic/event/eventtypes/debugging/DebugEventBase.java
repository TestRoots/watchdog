package nl.tudelft.watchdog.core.logic.event.eventtypes.debugging;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;

import java.util.Date;

public class DebugEventBase extends EventBase {

	/** Serial Id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public DebugEventBase(TrackingEventType type, Date timestamp) {
		super(type, timestamp);
	}

}
