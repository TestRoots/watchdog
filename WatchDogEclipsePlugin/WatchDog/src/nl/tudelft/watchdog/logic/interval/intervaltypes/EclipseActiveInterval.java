package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval in which Eclipse is the active window. */
public class EclipseActiveInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EclipseActiveInterval(Date start) {
		super(IntervalType.ECLIPSE_ACTIVE, start);
	}
}
