package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval in which Eclipse is the active application. */
public class EclipseActiveInterval extends IntervalBase {

	/** Constructor. */
	public EclipseActiveInterval(Date start) {
		super(IntervalType.ECLIPSE_ACTIVE, start);
	}

	/** Serial version id. */
	private static final long serialVersionUID = 1L;

}
