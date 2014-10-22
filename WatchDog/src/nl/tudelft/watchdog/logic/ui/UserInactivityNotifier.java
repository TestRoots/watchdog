package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;

/**
 * A Special InactivityNotifier that makes sure a wrapping
 * {@link EventType#USER_ACTIVITY} event exists if its trigger is called.
 */
class UserInactivityNotifier extends InactivityNotifier {
	private final IntervalManager intervalManager;

	/** Constructor. */
	public UserInactivityNotifier(EventManager eventManager,
			int activityTimeout, EventType type, IntervalManager intervalManager) {
		super(eventManager, activityTimeout, type);
		this.intervalManager = intervalManager;
	}

	/** Trigger accepting a forcedDate when the event should be fired. */
	public void trigger(Date forcedDate) {
		trigger();
		IntervalBase interval = intervalManager
				.getIntervalOfType(IntervalType.USER_ACTIVE);
		if (interval == null) {
			eventManager.update(
					new WatchDogEvent(this, EventType.USER_ACTIVITY),
					forcedDate);
		}

	}
}