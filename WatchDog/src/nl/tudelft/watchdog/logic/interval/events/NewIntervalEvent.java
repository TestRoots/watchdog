package nl.tudelft.watchdog.logic.interval.events;

import java.util.EventObject;

import nl.tudelft.watchdog.logic.interval.active.ActiveIntervalBase;

@SuppressWarnings("serial")
public class NewIntervalEvent extends EventObject {

	private ActiveIntervalBase interval;

	public NewIntervalEvent(ActiveIntervalBase source) {
		super(source);
		this.interval = source;
	}

	public ActiveIntervalBase getInterval() {
		return interval;
	}

}
