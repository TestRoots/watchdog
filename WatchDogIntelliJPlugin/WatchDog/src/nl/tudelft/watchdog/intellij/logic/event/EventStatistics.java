package nl.tudelft.watchdog.intellij.logic.event;

import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.EventStatisticsBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
        // Split events list into one list per event type.
        List<EventBase> bpAddEvents = new ArrayList<>();
        List<EventBase> bpChangeEvents = new ArrayList<>();
        List<EventBase> bpRemoveEvents = new ArrayList<>();
        // TODO: add other event types
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
            }
        }

        // Create and add the tasks for each event type.
        final TaskSeries allTasks = new TaskSeries("Debug Events");
        allTasks.add(
                createTaskForEventsWithName("Breakpoint Added", bpAddEvents));
        allTasks.add(createTaskForEventsWithName("Breakpoint Changed",
                bpChangeEvents));
        allTasks.add(createTaskForEventsWithName("Breakpoint Removed",
                bpRemoveEvents));
        // TODO: create task for other events

        // Create collection of the overall tasks.
        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(allTasks);
        return collection;
    }

    /**
     * Creates the overall task for a particular event type and attaches each
     * individual event as a subtask.
     */
    private Task createTaskForEventsWithName(String taskName,
                                             List<EventBase> events) {
        final Task overallTask;
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
            overallTask = new Task(taskName, new Date(),
                    addDeltaTo(new Date()));
        }
        return overallTask;
    }

}
