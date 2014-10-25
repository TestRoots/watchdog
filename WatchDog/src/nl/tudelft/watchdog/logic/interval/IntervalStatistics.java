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
	private static final int FILTERED_INTERVALS_IN_MINUTES = 60;

	private IntervalPersister intervalPersister;

	public Duration eclipseOpen;
	public Duration userActive;
	public Duration userReading;
	public Duration userTyping;
	public Duration userProduction;
	public Duration userTest;

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

	/** Filters out and removes intervals which are older than one hour. */
	private void filterIntervals() {
		ArrayList<IntervalBase> filteredIntervals = new ArrayList<IntervalBase>();
		ArrayList<IntervalBase> intervalsToRemove = new ArrayList<IntervalBase>();

		mostRecentDate = intervals.get(intervals.size() - 1).getEnd();
		DateTime thresholdDate = new DateTime(mostRecentDate);
		thresholdDate = thresholdDate
				.minusMinutes(FILTERED_INTERVALS_IN_MINUTES);

		for (IntervalBase interval : intervals) {
			if (interval.getEnd().after(thresholdDate.toDate())) {
				IntervalBase clonedInterval = (IntervalBase) interval.clone();
				if (interval.getStart().before(thresholdDate.toDate())) {
					clonedInterval.setStartTime(thresholdDate.toDate());
				}
				if (!clonedInterval.isClosed()) {
					clonedInterval.setEndTime(mostRecentDate);
				}
				if (clonedInterval.getDuration().isLongerThan(
						new Duration(1000 * 60 * 10))) {
					System.out.println("problem!");
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

	private void calculateStatistics() {
		eclipseOpen = aggregateDurations(getIntervalsOfType(IntervalType.ECLIPSE_OPEN));
		userActive = aggregateDurations(getIntervalsOfType(IntervalType.USER_ACTIVE));
		userReading = aggregateDurations(getIntervalsOfType(IntervalType.READING));
		userTyping = aggregateDurations(getIntervalsOfType(IntervalType.TYPING));
		userTest = aggregateDurations(getEditorIntervalsOfDocType(DocumentType.TEST));
		userProduction = aggregateDurations(getEditorIntervalsOfDocType(DocumentType.PRODUCTION));
		performDataSanitation();
	}

	private void performDataSanitation() {
		Duration summarizedUserActivity = userReading.plus(userTyping);
		if (userActive.isShorterThan(summarizedUserActivity)) {
			userActive = summarizedUserActivity;
		}
		if (eclipseOpen.isShorterThan(userActive)) {
			eclipseOpen = userActive;
		}
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

	public double getPreciseTime(Duration duration) {
		return ((double) duration.getStandardSeconds() / 60);
	}
}
