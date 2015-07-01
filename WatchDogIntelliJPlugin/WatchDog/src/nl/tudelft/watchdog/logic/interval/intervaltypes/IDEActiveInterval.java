package nl.tudelft.watchdog.logic.interval.intervaltypes;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;

import java.util.Date;

/** Interval in which IntelliJ is the active window. */
public class IDEActiveInterval extends IntervalBase {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public IDEActiveInterval(Date start) {
        super(IntervalType.IDE_ACTIVE, start);
    }
}
