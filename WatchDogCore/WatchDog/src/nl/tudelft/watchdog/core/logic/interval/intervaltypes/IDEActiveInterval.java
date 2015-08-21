package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

/** Interval in which IDE is the active window. */
public class IDEActiveInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public IDEActiveInterval(Date start) {
		super(IntervalType.IDE_ACTIVE, start);
	}
}
