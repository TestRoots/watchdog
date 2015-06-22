package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval in which IntelliJ is the active window. */
public class IntelliJActiveInterval extends IntervalBase {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public IntelliJActiveInterval(Date start) {
        super(IntervalType.INTELLIJ_ACTIVE, start);
    }
}
