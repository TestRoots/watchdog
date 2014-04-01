package nl.tudelft.watchdog.logic.eclipseuireader.events.interval;

import java.util.EventObject;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;

@SuppressWarnings("serial")
public class NewIntervalEvent extends EventObject {

	private IntervalBase interval;

	public NewIntervalEvent(IntervalBase source) {
		super(source);
		this.interval = source;
	}

	public IntervalBase getInterval() {
		return interval;
	}

}
