package nl.tudelft.watchdog.logic.ui;

import java.util.Date;
import java.util.Timer;
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
public class TimeProgressChecker {

	private static int UPDATE_RATE = 1 * 60 * 1000;

	private Timer timer;

	private TimeProgressTimerTask task;

	/** Constructor. */
	public TimeProgressChecker(IntervalManager intervalManager,
			EventManager eventManager) {
		task = new TimeProgressTimerTask(intervalManager, eventManager);
		task.run();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(task, 0, UPDATE_RATE);
	}

	private static class TimeProgressTimerTask extends TimerTask {

		long previousExecutionDate;

		private final IntervalManager intervalManager;

		private EventManager eventManager;

		private TimeProgressTimerTask(IntervalManager intervalManager,
				EventManager eventManager) {
			this.intervalManager = intervalManager;
			this.eventManager = eventManager;
			previousExecutionDate = System.currentTimeMillis();
		}

		@Override
		public void run() {
			long executionDate = System.currentTimeMillis();
			long delta = executionDate - previousExecutionDate;
			boolean deltaWithinReasonableBoundaries = delta >= UPDATE_RATE
					&& delta <= UPDATE_RATE * 1.16;
			if (!deltaWithinReasonableBoundaries) {
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
