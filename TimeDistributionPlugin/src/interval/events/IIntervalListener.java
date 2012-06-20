package interval.events;

import java.util.EventListener;

public interface IIntervalListener extends EventListener {
	public void onNewInterval(IntervalEvent evt);
	public void onClosingInterval(IntervalEvent evt);
}
