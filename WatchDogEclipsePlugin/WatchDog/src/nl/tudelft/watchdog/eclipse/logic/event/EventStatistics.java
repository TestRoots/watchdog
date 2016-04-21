package nl.tudelft.watchdog.eclipse.logic.event;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.EventStatisticsBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;

/**
 * Contains the functionality to create a Dataset containing all events that
 * occurred during a certain debug interval.
 */
public class EventStatistics extends EventStatisticsBase {

	/** Constructor. */
	public EventStatistics(DebugEventManager debugEventManager,
			DebugInterval selectedInterval) {
		super(debugEventManager, selectedInterval);
	}

	/**
	 * Creates a dataset of all events that occurred during the selected debug
	 * interval.
	 */
	public GanttCategoryDataset createDebugEventGanttChartDataset() {
		splitEventsIntoAListPerType();

		// Create and add the tasks for each event type.
		TaskSeries allTasks = new TaskSeries("Debug Events");
		allTasks.add(
				createTaskForEventsWithName(bpAddEvents, "Breakpoint Added"));
		allTasks.add(createTaskForEventsWithName(bpChangeEvents,
				"Breakpoint Changed"));
		allTasks.add(createTaskForEventsWithName(bpRemoveEvents,
				"Breakpoint Removed"));
		allTasks.add(createTaskForEventsWithName(suspendBpEvents,
				"Suspended (breakpoint)"));
		allTasks.add(createTaskForEventsWithName(suspendClientEvents,
				"Suspended (client)"));
		allTasks.add(createTaskForEventsWithName(stepOutEvents, "Stepped Out"));
		allTasks.add(
				createTaskForEventsWithName(stepIntoEvents, "Stepped Into"));
		allTasks.add(
				createTaskForEventsWithName(stepOverEvents, "Stepped Over"));
		allTasks.add(createTaskForEventsWithName(resumeClientEvents,
				"Resumed (client)"));
		allTasks.add(createTaskForEventsWithName(inspectEvents,
				"Inspected Variable"));
		allTasks.add(createTaskForEventsWithName(defineWatchEvents,
				"Defined Watch"));
		allTasks.add(createTaskForEventsWithName(evalExpressionEvents,
				"Evaluated Expression"));
		allTasks.add(createTaskForEventsWithName(modVarValueEvents,
				"Modified Variable Value"));

		// Create collection of the overall tasks.
		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(allTasks);
		return collection;
	}

	/**
	 * Creates the overall task for a particular event type and attaches each
	 * individual event as a subtask.
	 */
	private Task createTaskForEventsWithName(List<EventBase> events,
			String taskName) {
		Task overallTask;
		if (!events.isEmpty()) {
			Collections.sort(events);
			overallTask = new Task(taskName, events.get(0).getTimestamp(),
					addDeltaTo(events.get(events.size() - 1).getTimestamp()));

			// Add subtask for each event
			for (EventBase event : events) {
				overallTask.addSubtask(
						new Task(event.toString(), event.getTimestamp(),
								addDeltaTo(event.getTimestamp())));
			}
		} else {
			overallTask = new Task(taskName, new Date(0), new Date(1));
		}
		return overallTask;
	}

}
