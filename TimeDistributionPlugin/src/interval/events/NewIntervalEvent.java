package interval.events;

import interval.ActiveInterval;

import java.util.EventObject;

@SuppressWarnings("serial")
public class NewIntervalEvent extends EventObject {

	private ActiveInterval interval;
	
	public NewIntervalEvent(ActiveInterval source) {
		super(source);	
		this.interval = source;
	}
	
	public ActiveInterval getInterval(){
		return interval;
	}

}
