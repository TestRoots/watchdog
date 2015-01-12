package nl.tudelft.watchdog.logic.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.util.WatchDogGlobals;
import nl.tudelft.watchdog.util.WatchDogLogger;

/**
 * Checks whether the time progress according to the system's time is in
 * accordance with its schedule. If not, closes all intervals for the last
 * recorded time and starts completely fresh intervals. Used to detect a system
 * suspend.
 */
public class TimeSynchronityChecker extends RegularCheckerBase {

	private static final int UPDATE_RATE = 1 * 60 * 1000;

	private final IntervalManager intervalManager;

	private final EventManager eventManager;

	/** Constructor. */
	public TimeSynchronityChecker(IntervalManager intervalManager,
			EventManager eventManager) {
		super(UPDATE_RATE);
		this.intervalManager = intervalManager;
		this.eventManager = eventManager;
		timer = new Timer(true);
		startTimeCheckerOnce();
	}

	/** Subclasses call this method from their constructor. */
	protected void startTimeCheckerOnce() {
		if (task != null) {
			task.cancel();
		}
		task = new TimeSynchronityTimerTask();
		timer.schedule(task, updateRate);
	}

	private class TimeSynchronityTimerTask extends TimerTask {

		long previousExecutionDate;

		private TimeSynchronityTimerTask() {
			previousExecutionDate = System.currentTimeMillis();
		}

		@Override
		public void run() {
			if (WatchDogGlobals.isActive) {
				// do not execute this task unless WatchDog has properly boot-up

				long executionDate = System.currentTimeMillis();
				long delta = executionDate - previousExecutionDate;
				boolean deltaIsWithinReasonableBoundaries = delta >= UPDATE_RATE
						&& delta <= UPDATE_RATE * 1.16;
				if (!deltaIsWithinReasonableBoundaries) {
					WatchDogLogger.getInstance().logInfo(
							"System suspend detected!");
					Perspective openedPerspective = intervalManager
							.getInterval(PerspectiveInterval.class)
							.getPerspectiveType();
					intervalManager.closeAllIntervals(new Date(
							previousExecutionDate + UPDATE_RATE));
					eventManager.update(new WatchDogEvent(this,
							EventType.START_ECLIPSE));
					eventManager.update(new WatchDogEvent(openedPerspective,
							EventType.START_PERSPECTIVE));
				}
			}

			startTimeCheckerOnce();
			previousExecutionDate = System.currentTimeMillis();
		}
	}

}
