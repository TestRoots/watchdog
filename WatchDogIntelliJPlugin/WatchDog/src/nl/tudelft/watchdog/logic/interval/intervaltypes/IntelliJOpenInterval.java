package nl.tudelft.watchdog.logic.interval.intervaltypes;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;

import java.util.Date;

/** Interval for open IntelliJ sessions. */
public class IntelliJOpenInterval extends IntervalBase {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public IntelliJOpenInterval(Date start) {
        super(IntervalType.IDE_OPEN, start);
    }
}
