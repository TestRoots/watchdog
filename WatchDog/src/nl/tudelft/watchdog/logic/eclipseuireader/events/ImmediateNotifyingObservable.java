package nl.tudelft.watchdog.logic.eclipseuireader.events;

import java.util.Observable;

/**
 * An {@link Observable} that immediately notifies each of its observers
 * immediately.
 */
public class ImmediateNotifyingObservable extends Observable {

	/**
	 * Notifies each subscribed observers immediately, pushing the changedObject
	 * to them.
	 */
	@Override
	public void notifyObservers(Object changedObject) {
		setChanged();
		super.notifyObservers(changedObject);
	}
}
