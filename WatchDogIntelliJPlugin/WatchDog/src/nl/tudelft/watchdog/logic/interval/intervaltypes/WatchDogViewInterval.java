package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;
// TODO implement listener
/** When the WatchDogView is open. */
public class WatchDogViewInterval extends IntervalBase {

	/** Constructor. */
	public WatchDogViewInterval(Date start) {
		super(IntervalType.WATCHDOGVIEW, start);
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

}
