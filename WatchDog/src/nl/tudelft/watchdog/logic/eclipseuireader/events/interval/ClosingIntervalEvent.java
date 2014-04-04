package nl.tudelft.watchdog.logic.eclipseuireader.events.interval;

import java.util.EventObject;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;

public class ClosingIntervalEvent extends EventObject {
	private IntervalBase interval;

	public ClosingIntervalEvent(IntervalBase source) {
		super(source);
		this.interval = source;
	}

	public IntervalBase getInterval() {
		return interval;
	}
}
