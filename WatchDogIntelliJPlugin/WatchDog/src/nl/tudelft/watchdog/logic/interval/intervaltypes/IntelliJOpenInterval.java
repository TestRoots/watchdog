package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Date;

/** Interval for open IntelliJ sessions. */
public class IntelliJOpenInterval extends IntervalBase {

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Constructor. */
    public IntelliJOpenInterval(Date start) {
        super(IntervalType.INTELLIJ_OPEN, start);
    }
}
