package nl.tudelft.watchdog.logic.interval.active;


/** Interval for the active session. */
public class SessionInterval extends IntervalBase {

	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public SessionInterval(long sessionSeed) {
		super(IntervalType.Session, sessionSeed);
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.Session;
	}

}
