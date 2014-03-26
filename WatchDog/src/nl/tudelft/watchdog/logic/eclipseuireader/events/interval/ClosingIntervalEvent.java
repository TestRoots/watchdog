package nl.tudelft.watchdog.logic.eclipseuireader.events.interval;

import java.util.EventObject;

import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;

@SuppressWarnings("serial")
public class ClosingIntervalEvent extends EventObject {
	private RecordedInterval interval;

	public ClosingIntervalEvent(RecordedInterval source) {
		super(source);
		this.interval = source;
	}

	public RecordedInterval getInterval() {
		return interval;
	}
}
