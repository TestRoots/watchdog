package nl.tudelft.watchdog.core.logic.interval.intervaltypes;

import java.util.Date;

/** When the WatchDogView is open. */
public class WatchDogViewInterval extends IntervalBase {

	/** Constructor. */
	public WatchDogViewInterval(Date start) {
		super(IntervalType.WATCHDOGVIEW, start);
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

}
