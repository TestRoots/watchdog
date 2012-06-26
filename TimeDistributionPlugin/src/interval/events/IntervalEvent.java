package interval.events;

import interval.IInterval;

import java.util.EventObject;

@SuppressWarnings("serial")
public class IntervalEvent extends EventObject {

	private IInterval interval;
	
	public IntervalEvent(IInterval source) {
		super(source);	
		this.interval = source;
	}
	
	public IInterval getInterval(){
		return interval;
	}

}
