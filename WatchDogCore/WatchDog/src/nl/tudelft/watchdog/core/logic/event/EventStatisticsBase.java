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
	protected final List<EventBase> events = new ArrayList<EventBase>();

	/** Lists of events per type. */
	protected List<EventBase> bpAddEvents = new ArrayList<EventBase>();
	protected List<EventBase> bpChangeEvents = new ArrayList<EventBase>();
	protected List<EventBase> bpRemoveEvents = new ArrayList<EventBase>();
	protected List<EventBase> suspendBpEvents = new ArrayList<EventBase>();
	protected List<EventBase> suspendClientEvents = new ArrayList<EventBase>();
	protected List<EventBase> stepOutEvents = new ArrayList<EventBase>();
	protected List<EventBase> stepIntoEvents = new ArrayList<EventBase>();
	protected List<EventBase> stepOverEvents = new ArrayList<EventBase>();
	protected List<EventBase> resumeClientEvents = new ArrayList<EventBase>();
	protected List<EventBase> inspectEvents = new ArrayList<EventBase>();
	protected List<EventBase> defineWatchEvents = new ArrayList<EventBase>();
	protected List<EventBase> evalExpressionEvents = new ArrayList<EventBase>();
	protected List<EventBase> modVarValueEvents = new ArrayList<EventBase>();

	/** The debug interval that is currently selected. */
	private final DebugInterval selectedInterval;

	/** Constructor. */
	public EventStatisticsBase(DebugEventManager debugEventManager, DebugInterval selectedInterval) {
		this.eventsStatisticsPersister = debugEventManager.getEventStatisticsPersister();
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
	 * Splits the events list into one list per event type.
	 */
	protected void splitEventsIntoAListPerType() {
		for (EventBase event : events) {
			switch (event.getType()) {
			case BREAKPOINT_ADD:
				bpAddEvents.add(event);
				break;
			case BREAKPOINT_CHANGE:
				bpChangeEvents.add(event);
				break;
			case BREAKPOINT_REMOVE:
				bpRemoveEvents.add(event);
				break;
			case SUSPEND_BREAKPOINT:
				suspendBpEvents.add(event);
				break;
			case SUSPEND_CLIENT:
				suspendClientEvents.add(event);
				break;
			case STEP_OUT:
				stepOutEvents.add(event);
				break;
			case STEP_INTO:
				stepIntoEvents.add(event);
				break;
			case STEP_OVER:
				stepOverEvents.add(event);
				break;
			case RESUME_CLIENT:
				resumeClientEvents.add(event);
				break;
			case INSPECT_VARIABLE:
				inspectEvents.add(event);
				break;
			case DEFINE_WATCH:
				defineWatchEvents.add(event);
				break;
			case EVALUATE_EXPRESSION:
				evalExpressionEvents.add(event);
				break;
			case MODIFY_VARIABLE_VALUE:
				modVarValueEvents.add(event);
				break;
			}
		}
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
