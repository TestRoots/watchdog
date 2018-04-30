package nl.tudelft.watchdog.core.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;

/**
 * A Special InactivityNotifier that makes sure a wrapping
 * {@link WatchDogEventType#USER_ACTIVITY} event exists if its trigger is called.
 */
public class UserInactivityNotifier extends InactivityNotifier {

	/** Constructor. */
	public UserInactivityNotifier(int activityTimeout, WatchDogEventType type) {
		super(activityTimeout, type);
	}

	@Override
	public void trigger(Date forcedDate) {
		trigger();
		WatchDogEventType.USER_ACTIVITY.process(forcedDate,this);
	}
}
