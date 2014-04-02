package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;

/** Interval for the active session. */
public class SessionInterval extends IntervalBase {

	/** Constructor. */
	public SessionInterval() {
		super(IntervalType.Session);
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
