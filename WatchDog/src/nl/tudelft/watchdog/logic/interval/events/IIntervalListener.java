package nl.tudelft.watchdog.logic.interval.events;

import java.util.EventListener;

public interface IIntervalListener extends EventListener {
	public void onNewInterval(NewIntervalEvent evt);

	public void onClosingInterval(ClosingIntervalEvent evt);
}
