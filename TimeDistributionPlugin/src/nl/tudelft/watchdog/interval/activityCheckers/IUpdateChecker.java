package nl.tudelft.watchdog.interval.activityCheckers;

import nl.tudelft.watchdog.exceptions.EditorClosedPrematurelyException;


public interface IUpdateChecker {

	public abstract boolean hasChanged() throws EditorClosedPrematurelyException;

}