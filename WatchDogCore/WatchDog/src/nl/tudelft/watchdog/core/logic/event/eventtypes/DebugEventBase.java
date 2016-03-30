package nl.tudelft.watchdog.core.logic.event.eventtypes;

import java.util.Date;

public class DebugEventBase extends EventBase {

	/** Serial Id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public DebugEventBase(EventType type, Date timestamp) {
		super(type, timestamp);
	}

}
