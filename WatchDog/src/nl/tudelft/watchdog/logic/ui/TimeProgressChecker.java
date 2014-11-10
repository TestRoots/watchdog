package nl.tudelft.watchdog.logic.ui;

import java.util.Date;
import java.util.TimerTask;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.util.WatchDogLogger;

/**
 * Checks whether the time progress according to the system's time is in
 * accordance with its schedule. If not, closes all intervals for the last
 * recorded time and starts completely fresh intervals. Used to detect a system
 * suspend.
 */
public class TimeProgressChecker extends RegularCheckerBase {

	private static int UPDATE_RATE = 1 * 60 * 1000;

	private static IntervalManager intervalManager;

	private static EventManager eventManager;

	/** Constructor. */
	public TimeProgressChecker(IntervalManager intervalManager,
			EventManager eventManager) {
		super(UPDATE_RATE);
		this.task = new TimeProgressTimerTask();
		TimeProgressChecker.intervalManager = intervalManager;
		TimeProgressChecker.eventManager = eventManager;
		setupAndStartTimeChecker();
	}

	private static class TimeProgressTimerTask extends TimerTask {

		long previousExecutionDate;

		private TimeProgressTimerTask() {
			previousExecutionDate = System.currentTimeMillis();
		}

		@Override
		public void run() {
			long executionDate = System.currentTimeMillis();
			long delta = executionDate - previousExecutionDate;
			boolean deltaIsWithinReasonableBoundaries = delta >= UPDATE_RATE
					&& delta <= UPDATE_RATE * 1.16;
			if (!deltaIsWithinReasonableBoundaries) {
				WatchDogLogger.getInstance()
						.logInfo("System suspend detected!");
				Perspective openedPerspective = intervalManager
						.getIntervalOfClass(PerspectiveInterval.class)
						.getPerspectiveType();
				intervalManager.closeAllIntervals(new Date(
						previousExecutionDate + UPDATE_RATE));
				eventManager.update(new WatchDogEvent(null,
						EventType.START_ECLIPSE));
				eventManager.update(new WatchDogEvent(openedPerspective,
						EventType.START_PERSPECTIVE));
			}

			previousExecutionDate = System.currentTimeMillis();
		}
	}

}
