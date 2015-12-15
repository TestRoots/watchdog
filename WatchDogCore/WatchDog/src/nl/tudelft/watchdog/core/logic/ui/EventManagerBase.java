package nl.tudelft.watchdog.core.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;

public interface EventManagerBase {
	
	void update(WatchDogEvent event);
	
	void update(WatchDogEvent event, Date forcedDate);

}
