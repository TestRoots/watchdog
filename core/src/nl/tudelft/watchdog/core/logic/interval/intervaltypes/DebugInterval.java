package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

/** Interval for debugging sessions. */
public class DebugInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public DebugInterval(Date start) {
		super(IntervalType.DEBUG, start);
	}

}
