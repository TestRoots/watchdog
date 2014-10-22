package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;

class UserInactivityNotifier extends InactivityNotifier {
	private final IntervalManager intervalManager;

	UserInactivityNotifier(EventManager eventManager,
			int activityTimeout, EventType type,
			IntervalManager intervalManager) {
		super(eventManager, activityTimeout, type);
		this.intervalManager = intervalManager;
	}

	public void trigger(Date forcedDate) {
		trigger();
		IntervalBase interval = intervalManager
				.getIntervalOfType(IntervalType.USER_ACTIVE);
		if (interval == null) {
			eventManager.update(new WatchDogEvent(this,
					EventType.USER_ACTIVITY), forcedDate);
		}

	}
}