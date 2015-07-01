package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;

/** Interval for open Eclipse sessions. */
public class EclipseOpenInterval extends IntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public EclipseOpenInterval(Date start) {
		super(IntervalType.IDE_OPEN, start);
	}
}
