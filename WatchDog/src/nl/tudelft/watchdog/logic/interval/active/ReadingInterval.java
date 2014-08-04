package nl.tudelft.watchdog.logic.interval.active;

import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.ReadingCheckerTask;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.ui.IWorkbenchPart;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends UserActivityIntervalBase {
	// TODO Add inactive checker

	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ReadingInterval(IWorkbenchPart part, long sessionSeed) {
		super(part, IntervalType.Reading, sessionSeed);
		checkForChangeTimer = new Timer();
		task = new ReadingCheckerTask(this.getEditor());
		checkForChangeTimer.schedule(new ReadingCheckerTask(this.getEditor()),
				0, WatchDogGlobals.READING_TIMEOUT);
	}

}
