package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval in which the user is active. */
public class UserActiveInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public UserActiveInterval(Date start) {
		super(IntervalType.USER_ACTIVE, start);
	}
}
