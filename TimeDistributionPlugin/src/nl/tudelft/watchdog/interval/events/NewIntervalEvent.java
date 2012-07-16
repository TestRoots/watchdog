package nl.tudelft.watchdog.interval.events;


import java.util.EventObject;

import nl.tudelft.watchdog.interval.active.ActiveInterval;

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
