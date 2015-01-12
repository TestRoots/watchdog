package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** An interval in which the user was active. */
public class UserActiveInterval extends IntervalBase {

	/** Constructor. */
	public UserActiveInterval(Date start) {
		super(IntervalType.USER_ACTIVE, start);
	}

	/** Serial version id. */
	private static final long serialVersionUID = 1L;

}
