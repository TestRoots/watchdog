package nl.tudelft.watchdog.logic.interval.activityCheckers;

import java.util.TimerTask;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;

public abstract class CheckerTimerTask extends TimerTask {

	/** An update checker. */
	protected IUpdateChecker stillActiveChecker;

	public interface IUpdateChecker {

		public abstract boolean hasChanged()
				throws EditorClosedPrematurelyException, ContentReaderException;

	}

}