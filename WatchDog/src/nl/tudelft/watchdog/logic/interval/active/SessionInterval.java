package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;

/** Interval for the active session. */
public class SessionInterval extends IntervalBase {

	/** Constructor. */
	public SessionInterval(String userid, long sessionSeed) {
		super(IntervalType.Session, userid, sessionSeed);
	}

	@Override
	public void listenForReactivation() {
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.Session;
	}

	@Override
	public void addTimeoutListener(long timeout,
			OnInactiveCallback callbackWhenFinished) {
	}

}
