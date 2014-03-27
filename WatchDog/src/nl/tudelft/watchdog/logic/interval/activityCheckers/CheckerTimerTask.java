package nl.tudelft.watchdog.logic.interval.activityCheckers;

import java.util.TimerTask;

public abstract class CheckerTimerTask extends TimerTask {

	/** Creates the listener for reactivation of this checker. */
	public abstract void createListenerForReactivation();

}