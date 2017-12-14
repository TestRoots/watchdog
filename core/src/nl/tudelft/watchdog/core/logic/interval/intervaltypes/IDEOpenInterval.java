package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

/** Interval for open IDE sessions. */
public class IDEOpenInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public IDEOpenInterval(Date start) {
		super(IntervalType.IDE_OPEN, start);
	}
}
