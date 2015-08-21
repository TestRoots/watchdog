package nl.tudelft.watchdog.intellij.logic.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;

/**
 * A performance-optimized notifier for a timeout when its {@link #trigger()}
 * was not called for a given timeout. When an inactivity is detected, an
 * inactivityEvent is fired. The type of this event can be specified.
 * 
 * Performance optimization: only update activity timer every second. This means
 * that we have 10% imprecision, ie. the user may have actually stayed inactive
 * shorter than we think he did.
 */
/* package */class InactivityNotifier {
	/** */
	protected final EventManager eventManager;

	private int activityTimeout;

	private Timer activityTimer;

	private ActivityTimerTask activityTimerTask;

	private EventType eventType;

	private boolean isRunning;

	/** Constructor. */
	public InactivityNotifier(EventManager eventManager, int activityTimeout,
			EventType type) {
		this.eventManager = eventManager;
		this.activityTimeout = activityTimeout;
		this.eventType = type;
		this.isRunning = false;
	}

	/**
	 * Triggers the timer, i.e. prolongs its lifetime or creates a new timer if
	 * non is running.
	 */
	public void trigger() {
		if (activityTimer == null || !isRunning) {
			createNewTimer();
		} else if (activityTimerTask.scheduledExecutionTime()
				- System.currentTimeMillis() < 0.9 * activityTimeout) {
			// Performance optimization: only update activity timer every
			// second. This means that we have 10% imprecision, ie. the user
			// may have actually stayed inactive shorter than we think he
			// did. Reduces the number of calls to createNewTimer().
			activityTimer.cancel();
			activityTimerTask.cancel();
			createNewTimer();
		}
	}

	private void createNewTimer() {
		activityTimer = new Timer(true);
		activityTimerTask = new ActivityTimerTask();
		activityTimer.schedule(activityTimerTask, activityTimeout);
		isRunning = true;
	}

	/**
	 * Immediately cancels the timer, sending an inactivity event.
	 * 
	 * @param forcedDate
	 *            the Date which should be supplied to the eventmanager for when
	 *            this event actually happened.
	 */
	public void cancelTimer(Date forcedDate) {
		if (!isRunning) {
			return;
		}
		activityTimerTask.run(forcedDate);
		activityTimer.cancel();
		activityTimerTask.cancel();
	}

	private class ActivityTimerTask extends TimerTask {

		protected void run(Date forcedDate) {
			eventManager.update(new WatchDogEvent(this, eventType), forcedDate);
			isRunning = false;
		}

		@Override
		public void run() {
			eventManager.update(new WatchDogEvent(this, eventType));
			isRunning = false;
		}
	}

}