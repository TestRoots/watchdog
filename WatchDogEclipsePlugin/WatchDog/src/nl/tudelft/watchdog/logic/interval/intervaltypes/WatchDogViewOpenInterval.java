/**
 * 
 */
package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.ui.WatchDogView;

/** When the {@link WatchDogView} is open. */
public class WatchDogViewOpenInterval extends IntervalBase {

	/** Constructor. */
	public WatchDogViewOpenInterval(Date start) {
		super(IntervalType.WATCHDOGVIEW_OPEN, start);
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

}
