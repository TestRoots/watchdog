package nl.tudelft.watchdog.logic.interval;

import java.util.Observable;
import java.util.Observer;

import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.ClosingIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.NewIntervalEvent;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

/**
 * An Interval Logger Observer which logs every new interval being opened and
 * closed to the {@link WatchDogLogger}.
 */
/* package */class IntervalLoggerObserver implements Observer {
	@Override
	public void update(Observable o, Object event) {
		if (event instanceof NewIntervalEvent) {
			NewIntervalEvent intervalEvent = (NewIntervalEvent) event;
			WatchDogLogger.getInstance().logInfo(
					"New interval: " + intervalEvent.getInterval());

		} else if (event instanceof ClosingIntervalEvent) {
			ClosingIntervalEvent intervalEvent = (ClosingIntervalEvent) event;
			WatchDogLogger.getInstance().logInfo(
					"Closing interval: "
							+ intervalEvent.getInterval().getDocument()
									.getFileName() + " \n "
							+ intervalEvent.getInterval().getStart() + " - "
							+ intervalEvent.getInterval().getEnd());
		}
	}
}