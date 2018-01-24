package nl.tudelft.watchdog.core.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;

/**
 * A Special InactivityNotifier that makes sure a wrapping
 * {@link WatchDogEvent.EventType#USER_ACTIVITY} event exists if its trigger is called.
 */
public class UserInactivityNotifier extends InactivityNotifier {

	/** Constructor. */
	public UserInactivityNotifier(int activityTimeout, WatchDogEvent.EventType type) {
		super(activityTimeout, type);
	}

	/** Trigger accepting a forcedDate when the event should be fired. */
	public void trigger(Date forcedDate) {
		trigger();
		new WatchDogEvent(this, WatchDogEvent.EventType.USER_ACTIVITY).update(forcedDate);
	}
}