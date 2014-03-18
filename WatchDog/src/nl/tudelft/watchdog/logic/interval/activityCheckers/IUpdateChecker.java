package nl.tudelft.watchdog.logic.interval.activityCheckers;

import nl.tudelft.watchdog.logic.exceptions.ContentReaderException;
import nl.tudelft.watchdog.logic.exceptions.EditorClosedPrematurelyException;

public interface IUpdateChecker {

	public abstract boolean hasChanged()
			throws EditorClosedPrematurelyException, ContentReaderException;

}