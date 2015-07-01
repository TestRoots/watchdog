package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.ui.WatchDogView;

/** When the {@link WatchDogView} is open. */
public class WatchDogViewInterval extends IntervalBase {

	/** Constructor. */
	public WatchDogViewInterval(Date start) {
		super(IntervalType.WATCHDOGVIEW, start);
	}

	/** Class version. */
	private static final long serialVersionUID = 1L;

}
