package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval in which eclipse is opened and running on the user's computer. */
public class EclipseOpenInterval extends IntervalBase {

	/** Constructor. */
	public EclipseOpenInterval(Date start) {
		super(IntervalType.ECLIPSE_OPEN, start);
	}

	/** Serial version id. */
	private static final long serialVersionUID = 1L;

}
