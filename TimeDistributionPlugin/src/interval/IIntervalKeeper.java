package interval;

import java.util.List;

import interval.events.IIntervalListener;

/**
 * Keeps track of all intervals. Users of this interface can listen to events regarding intervals
 */
public interface IIntervalKeeper {
	void addIntervalListener(IIntervalListener listener);
	void removeIntervalListener(IIntervalListener listener);
	List<IInterval> getRecordedIntervals();
	
}