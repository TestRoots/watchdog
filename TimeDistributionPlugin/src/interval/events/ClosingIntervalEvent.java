package interval.events;

import interval.RecordedInterval;

import java.util.EventObject;

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
