package nl.tudelft.watchdog.logic.eclipseuireader.events;

import java.util.Observable;

public class EventObservable extends Observable {
	@Override
	public void notifyObservers(Object arg) {
		setChanged();
		super.notifyObservers(arg);
	}
}
