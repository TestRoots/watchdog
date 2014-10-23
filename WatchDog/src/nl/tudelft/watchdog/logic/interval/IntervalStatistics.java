package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/** Gathers and calculates statistics on interval length. */
@SuppressWarnings("javadoc")
public class IntervalStatistics extends IntervalManagerBase {
	private IntervalPersister intervalPersister;

	public Duration eclipseOpen;
	public Duration userActive;
	public Duration userReading;
	public Duration userTyping;
	public Duration userProduction;
	public Duration userTesting;

	public Date mostRecentDate;
	public Date oldestDate;

	/** Constructor. */
	public IntervalStatistics(IntervalManager intervalManager) {
		intervalPersister = intervalManager.getIntervalsStatisticsPersister();
		intervals.addAll(intervalPersister.readIntervals());
		intervals.addAll(intervalManager.getOpenIntervals());
		filterIntervals();
		calculateStatistics();
	}

	private void calculateStatistics() {
		eclipseOpen = aggregateDurations(getIntervalsOfType(IntervalType.ECLIPSE_OPEN));
		userActive = aggregateDurations(getIntervalsOfType(IntervalType.USER_ACTIVE));
		userReading = aggregateDurations(getIntervalsOfType(IntervalType.READING));
		userTyping = aggregateDurations(getIntervalsOfType(IntervalType.TYPING));
		userTesting = aggregateDurations(getEditorIntervalsOfDocType(DocumentType.TEST));
		userProduction = aggregateDurations(getEditorIntervalsOfDocType(DocumentType.PRODUCTION));
	}

	/** Filters out and removes intervals which are older than one hour. */
	private void filterIntervals() {
		ArrayList<IntervalBase> filteredIntervals = new ArrayList<IntervalBase>();
		ArrayList<IntervalBase> intervalsToRemove = new ArrayList<IntervalBase>();

		mostRecentDate = intervals.get(intervals.size() - 1).getEnd();
		DateTime thresholdDate = new DateTime(mostRecentDate);
		thresholdDate = thresholdDate.minusMinutes(2);

		for (IntervalBase interval : intervals) {
			if (interval.getEnd().after(thresholdDate.toDate())) {
				IntervalBase clonedInterval = (IntervalBase) interval.clone();
				if (interval.getStart().before(thresholdDate.toDate())) {
					clonedInterval.setStartTime(thresholdDate.toDate());
				}
				filteredIntervals.add(clonedInterval);
			} else {
				intervalsToRemove.add(interval);
			}
		}

		oldestDate = filteredIntervals.get(0).getStart();
		intervalPersister.removeIntervals(intervalsToRemove);
		intervals = filteredIntervals;
	}

	private Duration aggregateDurations(List<IntervalBase> intervals) {
		Duration aggregatedDuration = new Duration(0);
		for (IntervalBase interval : intervals) {
			aggregatedDuration = aggregatedDuration
					.plus(interval.getDuration());
		}
		return aggregatedDuration;
	}

	/** @return the number of intervals. */
	public int getNumberOfIntervals() {
		return intervals.size();
	}
}
