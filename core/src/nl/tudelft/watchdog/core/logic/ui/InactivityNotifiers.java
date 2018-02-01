package nl.tudelft.watchdog.core.logic.ui;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;

import java.util.Date;

public enum InactivityNotifiers {

    USER_INACTIVITY(new UserInactivityNotifier(WatchDogGlobals.getUserInactivityTimeoutDuration(), WatchDogEventType.USER_INACTIVITY)),
    READING(new InactivityNotifier(WatchDogGlobals.getUserInactivityTimeoutDuration(), WatchDogEventType.READING_INACTIVITY)),
    TYPING(new InactivityNotifier(WatchDogGlobals.getUserInactivityTimeoutDuration(), WatchDogEventType.TYPING_INACTIVITY));

    private InactivityNotifier inactivityNotifier;

    InactivityNotifiers(InactivityNotifier inactivityNotifier) {
        this.inactivityNotifier = inactivityNotifier;
    }

    public void trigger(Date forcedDate) {
        this.inactivityNotifier.trigger(forcedDate);
    }

    public void cancelTimer(Date forcedDate) {
        this.inactivityNotifier.cancelTimer(forcedDate);
    }

    public void trigger() {
        this.inactivityNotifier.trigger();
    }

    /**
     * Only used for tests, as the notifiers have a different timeout there.
     * @param inactivityNotifier The new notifier.
     */
    public void updateNotifier(InactivityNotifier inactivityNotifier) {
        this.inactivityNotifier = inactivityNotifier;
    }
}
