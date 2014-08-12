package nl.tudelft.watchdog.logic.interval.intervaltypes;

/** Interval for the active session. */
public class SessionInterval extends IntervalBase {

	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public SessionInterval(long sessionSeed) {
		super(IntervalType.ECLIPSE_OPEN, sessionSeed);
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.ECLIPSE_OPEN;
	}

}
