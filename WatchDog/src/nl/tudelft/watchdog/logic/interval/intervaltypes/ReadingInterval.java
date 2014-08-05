package nl.tudelft.watchdog.logic.interval.intervaltypes;

import java.util.Timer;

import nl.tudelft.watchdog.logic.interval.activityCheckers.ReadingCheckerTask;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.ui.IWorkbenchPart;

/** A reading interval, i.e. an interval in which the user read some code. */
public class ReadingInterval extends UserActivityIntervalBase {
	/** Serial id. */
	private static final long serialVersionUID = 1L;

	/** Constructor. */
	public ReadingInterval(IWorkbenchPart part, long sessionSeed) {
		super(part, IntervalType.Reading, sessionSeed);
		timer = new Timer();
		stillActiveCheckerTask = new ReadingCheckerTask(this.getEditor(), this);
		timer.schedule(stillActiveCheckerTask, 0,
				WatchDogGlobals.READING_TIMEOUT);
	}

}
