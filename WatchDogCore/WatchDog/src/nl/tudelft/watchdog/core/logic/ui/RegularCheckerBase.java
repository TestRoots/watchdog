package nl.tudelft.watchdog.core.logic.ui;

import java.util.Timer;
import java.util.TimerTask;

/** A checker that is being run every n-miliseconds. */
public abstract class RegularCheckerBase {
	/**
	 * The rate in miliseconds at which the timer task is regularly scheduled
	 * for execution.
	 */
	protected int updateRate;

	/** The timer */
	protected Timer timer;

	/** The actual task to be run. Needs to be set by subclasses. */
	protected TimerTask task;

	/** Constructor. */
	public RegularCheckerBase(int updateRate) {
		this.updateRate = updateRate;
	}

	/** Subclasses call this method from their constructor. */
	protected void runSetupAndStartTimeChecker() {
		task.run();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(task, updateRate, updateRate);
	}
}
