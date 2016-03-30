package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

public class DebugEvent extends EventBase {

	/** Serial Id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public DebugEvent(EventType type, Date timestamp) {
		super(type, timestamp);
	}

}
