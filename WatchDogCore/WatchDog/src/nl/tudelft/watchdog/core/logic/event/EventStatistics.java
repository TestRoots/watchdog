package nl.tudelft.watchdog.core.logic.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * Contains the functionality to create a Dataset containing all events that
 * occurred during a certain debug interval.
 */
public class EventStatistics {

	/** Persister storing all events. */
	private final PersisterBase eventsStatisticsPersister;

	/** A list of the managed events. */
	private final List<EventBase> events = new ArrayList<>();
	
	/** The debug interval that is currently selected. */
	private final DebugInterval selectedInterval;

	/** Constructor. */
	public EventStatistics(EventManager eventManager, DebugInterval selectedInterval) {
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

	public GanttCategoryDataset createDebugEventGanttChartDataset() {
		// TODO Auto-generated method stub
		final TaskSeries s1 = new TaskSeries("debug events");
		s1.add(new Task("test event", new Date(1), new Date(2)));
		s1.add(new Task("test event2", new Date(3), new Date(4)));

		// NOTE: the start+end times of the parent task should be wide enough to
		// hold all subtasks
		Task ev3 = new Task("test event3", new Date(1), new Date(5));
		ev3.addSubtask(new Task("test event3.2", new Date(4), new Date(5)));
		ev3.addSubtask(new Task("test event3.1", new Date(2), new Date(3)));
		s1.add(ev3);

		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);
		return collection;
	}

}
