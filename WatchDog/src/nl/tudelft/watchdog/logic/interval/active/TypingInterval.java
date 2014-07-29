package nl.tudelft.watchdog.logic.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;
import nl.tudelft.watchdog.logic.interval.activityCheckers.TypingCheckerTask;

import org.eclipse.ui.IWorkbenchPart;

/**
 * An interval for when the user is currently typing, connected to the
 * {@link IntervalType#Typing} activity.
 */
public class TypingInterval extends UserActivityIntervalBase {

	/** Constructor. */
	public TypingInterval(IWorkbenchPart part, String userid, long sessionSeed) {
		super(part, IntervalType.Typing, userid, sessionSeed);
		checkForChangeTimer = new Timer();
	}

	@Override
	public void addTimeoutListener(long timeout,
			OnInactiveCallback callbackWhenFinished) {
		task = new TypingCheckerTask(this.getPart(), callbackWhenFinished);
		checkForChangeTimer.schedule(new TypingCheckerTask(this.getEditor(),
				callbackWhenFinished), timeout, timeout);
	}

	@Override
	public IntervalType getActivityType() {
		return IntervalType.Typing;
	}
}
