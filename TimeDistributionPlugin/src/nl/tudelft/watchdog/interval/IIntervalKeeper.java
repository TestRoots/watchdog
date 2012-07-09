package nl.tudelft.watchdog.interval;

import java.util.List;

import nl.tudelft.watchdog.interval.events.IIntervalListener;


/**
 * Keeps track of all intervals. Users of this interface can listen to events regarding intervals
 */
public interface IIntervalKeeper {
	void addIntervalListener(IIntervalListener listener);
	void removeIntervalListener(IIntervalListener listener);
	List<IInterval> getRecordedIntervals();
	void setRecordedIntervals(List<IInterval> intervals);
	
}