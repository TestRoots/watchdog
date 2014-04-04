package nl.tudelft.watchdog.logic.interval.activityCheckers;

import java.util.TimerTask;

public abstract class CheckerTimerTask extends TimerTask {

	/** An update checker. */
	protected IUpdateChecker checker;

	/** Creates the listener for reactivation of this checker. */
	public abstract void createListenerForReactivation();

}