package nl.tudelft.watchdog.ui.infoDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;

import org.joda.time.Duration;

/** Statistical computations on intervals. */
public class IntervalStatistics {

	/** The intervals */
	private List<IntervalBase> intervals;

	/** The total time recorded in activities. */
	private Duration totalTimeOverAllActivities;

	/**
	 * A map from the {@link IntervalType} to the time the activity was
	 * performed.
	 */
	private Map<IntervalType, Duration> activityTypeToDuration = new HashMap<>();

	/** Constructor. */
	public IntervalStatistics(List<IntervalBase> intervals) {
		this.intervals = intervals;
	}

	/**
	 * Calculates the total time for all activities and makes them accessible
	 * through this class.
	 */
	public void calculateDurations() {
		totalTimeOverAllActivities = new Duration(0);
		for (IntervalType activity : IntervalType.values()) {
			Duration activityTime = calculateTime(activity);
			activityTypeToDuration.put(activity, activityTime);
			if (activity != IntervalType.ECLIPSE_ACTIVE) {
				totalTimeOverAllActivities = totalTimeOverAllActivities
						.plus(activityTime);
			}
		}
	}

	/**
	 * Calculates and returns the total time for a certain activity in all the
	 * intervals.
	 */
	private Duration calculateTime(IntervalType activity) {
		Duration totalTime = new Duration(0);
		for (IntervalBase interval : intervals) {
			if (interval.getType().equals(activity)) {
				totalTime = totalTime.plus(interval.getDuration());
			}
		}
		return totalTime;
	}

	/**
	 * @return The duration of the given activity.
	 */
	public Duration getDurationOfAcitivity(IntervalType activity) {
		return activityTypeToDuration.get(activity);
	}

	/** @return The total time recorded in activities. */
	public Duration getTotalTimeOverAllActivities() {
		return totalTimeOverAllActivities;
	}

}
