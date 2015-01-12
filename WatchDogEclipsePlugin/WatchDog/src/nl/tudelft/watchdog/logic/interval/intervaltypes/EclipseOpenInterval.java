package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval for open Eclipse sessions. */
public class EclipseOpenInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EclipseOpenInterval(Date start) {
		super(IntervalType.ECLIPSE_OPEN, start);
	}
}
