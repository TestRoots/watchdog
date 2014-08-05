package nl.tudelft.watchdog.logic.interval.activityCheckers;

import java.util.TimerTask;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActivityIntervalBase;

public abstract class CheckerTimerTask extends TimerTask {

	public CheckerTimerTask(UserActivityIntervalBase interval) {
		this.interval = interval;
	}

	/** An update checker. */
	protected IUpdateChecker stillActiveChecker;

	protected UserActivityIntervalBase interval;

	public interface IUpdateChecker {

		public abstract boolean hasChanged()
				throws EditorClosedPrematurelyException, ContentReaderException;

	}

}