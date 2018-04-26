package nl.tudelft.watchdog.core.logic.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

/**
 * Contains basic functionality for selecting all events that occurred during a
 * certain debug interval. Dataset creation functionality should be implemented
 * by IDE-specific code in order to avoid IDE-specific dependencies in the core
 * project.
 */
public class EventStatistics {

	/**
	 * The amount of time before a debug interval of which the events should be
	 * included as well.
	 */
	public static final int PRE_SESSION_TIME_TO_INCLUDE = 20 * 1000;

	/** Persister storing all events. */
	private final PersisterBase eventsStatisticsPersister;

	/** A list of the managed events. */
	protected final List<EventBase> events = new ArrayList<EventBase>();

	/** The debug interval that is currently selected. */
	private final DebugInterval selectedInterval;

	/** The timestamp from which the events should be added. */
	private Date startOfEventSelection;

	/** Constructor. */
	public EventStatistics(TrackingEventManager trackingEventManager, DebugInterval selectedInterval) {
		this.eventsStatisticsPersister = trackingEventManager.getEventStatisticsPersister();
		this.selectedInterval = selectedInterval;
		startOfEventSelection = new Date(selectedInterval.getStart().getTime() - PRE_SESSION_TIME_TO_INCLUDE);
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
	 *         interval or in the period right before it.
	 */
	private boolean isWithinSelectedDebugInterval(EventBase event) {
		Date timestamp = event.getTimestamp();
		return startOfEventSelection.before(timestamp) && timestamp.before(selectedInterval.getEnd());
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

	/**
     * Creates a dataset of all events that occurred during the selected debug
     * interval.
     */
    public GanttCategoryDataset createDebugEventGanttChartDataset() {
        // Create and add the tasks for each event type.
        TaskSeries allTasks = new TaskSeries("Debug Events");

        for (TrackingEventType type : TrackingEventType.values()) {
            final List<EventBase> filteredEventList = events.stream().filter(e -> e.getType() == type).collect(Collectors.toList());
            allTasks.add(createTaskForEventsWithName(filteredEventList, type.getTextualDescription()));
        }

        // Create collection of the overall tasks.
        TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(allTasks);
        return collection;
    }

    /**
     * Creates the overall task for a particular event type and attaches each
     * individual event as a subtask.
     */
    private Task createTaskForEventsWithName(List<EventBase> events, String taskName) {
        if (events.isEmpty()) {
            return new Task(taskName, new Date(0), new Date(1));
        }
        Collections.sort(events);
        Task overallTask = new Task(taskName, events.get(0).getTimestamp(),
                addDeltaTo(events.get(events.size() - 1).getTimestamp()));

        // Add subtask for each event
        for (EventBase event : events) {
            final Task subtask = new Task(event.toString(), event.getTimestamp(),
                    addDeltaTo(event.getTimestamp()));
            overallTask.addSubtask(subtask);
        }
        return overallTask;
    }

}
