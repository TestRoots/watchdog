package nl.tudelft.watchdog.logic.interval;

import java.util.List;

import nl.tudelft.watchdog.logic.interval.events.IIntervalListener;
import nl.tudelft.watchdog.logic.interval.recorded.IInterval;

/**
 * Manages interval listeners and keeps track of all intervals. Implements the
 * observer pattern, ie. listeners can subscribe to interval events and will be
 * notified by an implementation of the {@link IIntervalManager}.
 */
public interface IIntervalManager {
	/** Registers a new interval listener. */
	public void addIntervalListener(IIntervalListener listener);

	/** Removes an existing interval listener. */
	public void removeIntervalListener(IIntervalListener listener);

	/** Returns a list of recorded intervals. */
	public List<IInterval> getRecordedIntervals();

	/** Sets a list of recorded intervals. */
	public void setRecordedIntervals(List<IInterval> intervals);

	/** Closes all currently open intervals. */
	public void closeAllCurrentIntervals();

}