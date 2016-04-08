package nl.tudelft.watchdog.core.logic.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * Contains basic functionality for selecting all events that occurred during a
 * certain debug interval. Dataset creation functionality should be implemented
 * by IDE-specific code in order to avoid IDE-specific dependencies in the core
 * project.
 */
public abstract class EventStatisticsBase {

	/** Persister storing all events. */
	private final PersisterBase eventsStatisticsPersister;

	/** A list of the managed events. */
	protected final List<EventBase> events = new ArrayList<>();

	/** The debug interval that is currently selected. */
	private final DebugInterval selectedInterval;

	/** Constructor. */
	public EventStatisticsBase(EventManager eventManager, DebugInterval selectedInterval) {
		this.eventsStatisticsPersister = eventManager.getEventStatisticsPersister();
		this.selectedInterval = selectedInterval;
		addAllEventsWithinSelectedInterval();
	}

	/** Fills the 'events' list with the correct events. */
	private void addAllEventsWithinSelectedInterval() {
		for (WatchDogItem item : eventsStatisticsPersister.readItems()) {
			if (item instanceof EventBase) {
				EventBase event = (EventBase) item;
				if (isWithinSelectedDebugInterval(event)) {
					events.add(event);
				}
			}
		}
	}

	/**
	 * @return true if and only if the event occurred within the selected debug
	 *         interval.
	 */
	private boolean isWithinSelectedDebugInterval(EventBase event) {
		Date timestamp = event.getTimestamp();
		return selectedInterval.getStart().before(timestamp) && timestamp.before(selectedInterval.getEnd());
	}

	/**
	 * Adds some time to make sure the end time of a task is later than its
	 * start time.
	 */
	protected Date addDeltaTo(Date timestamp) {
		Calendar newTimestamp = Calendar.getInstance();
		newTimestamp.setTime(timestamp);
		newTimestamp.add(Calendar.MILLISECOND, 500);
		return newTimestamp.getTime();
	}

}
