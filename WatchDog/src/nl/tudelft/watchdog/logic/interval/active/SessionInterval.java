package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;

/** Interval for the active session. */
public class SessionInterval extends IntervalBase {

	/** Constructor. */
	public SessionInterval() {
		super(ActivityType.EclipseOpen);
	}

	@Override
	public void listenForReactivation() {
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.EclipseOpen;
	}

	@Override
	public void addTimeoutListener(long timeout,
			OnInactiveCallback callbackWhenFinished) {
	}

}
