package nl.tudelft.watchdog.logic.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;
import nl.tudelft.watchdog.logic.interval.activityCheckers.ReadingCheckerTask;

import org.eclipse.ui.IWorkbenchPart;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends UserActivityIntervalBase {

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor in this interval
	 */
	public ReadingInterval(IWorkbenchPart part, String userid, long sessionSeed) {
		super(part, IntervalType.Reading, userid, sessionSeed);
		checkForChangeTimer = new Timer();
	}

	@Override
	public void addTimeoutListener(long timeout,
			final OnInactiveCallback callbackWhenFinished) {
		task = new ReadingCheckerTask(this.getEditor(), callbackWhenFinished);
		checkForChangeTimer.schedule(new ReadingCheckerTask(this.getEditor(),
				callbackWhenFinished), 0, timeout);
	}

	@Override
	public void listenForReactivation() {
		assert (task != null);
		task.createListenerForReactivation();
	}

}
