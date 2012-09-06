package nl.tudelft.watchdog.interval.activityCheckers;

import nl.tudelft.watchdog.exceptions.EditorClosedPrematurelyException;
import nl.tudelft.watchdog.exceptions.ContentReaderException;


public interface IUpdateChecker {

	public abstract boolean hasChanged() throws EditorClosedPrematurelyException, ContentReaderException;


}