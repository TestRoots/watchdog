package nl.tudelft.watchdog.interval.events;


import java.util.EventObject;

import nl.tudelft.watchdog.interval.RecordedInterval;

@SuppressWarnings("serial")
public class ClosingIntervalEvent extends EventObject {
	private RecordedInterval interval;
	
	public ClosingIntervalEvent(RecordedInterval source) {
		super(source);
		this.interval = source;
	}

	public RecordedInterval getInterval(){
		return interval;
	}
}
