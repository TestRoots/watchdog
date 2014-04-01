package nl.tudelft.watchdog.logic.interval.active;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;

/** Interval for the active session. */
public class SessionInterval extends IntervalBase {

	@Override
	public void listenForReactivation() {
	}

	@Override
	public ActivityType getActivityType() {
		return ActivityType.EclipseOpen;
	}

	@Override
	public void addTimeoutListener(long timeout,
			OnInactiveCallBack callbackWhenFinished) {
	}

}
