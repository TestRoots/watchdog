package nl.tudelft.watchdog.logic.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.TypingCheckerTask;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.ui.IWorkbenchPart;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#Typing} activity.
 */
public class TypingInterval extends UserActivityIntervalBase {

	/** Serial ID. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public TypingInterval(IWorkbenchPart part, long sessionSeed) {
		super(part, IntervalType.Typing, sessionSeed);
		checkForChangeTimer = new Timer();
		task = new TypingCheckerTask(this.getPart());
		checkForChangeTimer.schedule(new TypingCheckerTask(this.getEditor()),
				WatchDogGlobals.TYPING_TIMEOUT, WatchDogGlobals.TYPING_TIMEOUT);
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.Typing;
	}
}
