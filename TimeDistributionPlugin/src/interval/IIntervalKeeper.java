package interval;

import interval.events.IIntervalListener;

/**
 * Keeps track of all intervals. Users of this interface can listen to events regarding intervals
 */
public interface IIntervalKeeper {
	public abstract void addIntervalListener(IIntervalListener listener);
	public abstract void removeIntervalListener(IIntervalListener listener);
	
}