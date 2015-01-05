package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval.Perspective;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/** Gathers and calculates statistics on interval length. */
@SuppressWarnings("javadoc")
public class IntervalStatistics extends IntervalManagerBase {
	private static final int FILTERED_INTERVALS_IN_MINUTES = 60;

	private final IntervalPersister intervalPersister;

	public Duration eclipseOpen;
	public Duration userActive;
	public Duration userReading;
	public Duration userTyping;
	public Duration userProduction;
	public Duration userTest;
	public Duration perspectiveDebug;
	public Duration perspectiveJava;
	public Duration perspectiveOther;

	public Date mostRecentDate;
	public Date oldestDate;

	/** Constructor. */
	public IntervalStatistics(IntervalManager intervalManager) {
		intervalPersister = intervalManager.getIntervalsStatisticsPersister();
		addIntervals(intervalManager);
		filterIntervals();
		calculateStatistics();
	}

	private void addIntervals(IntervalManager intervalManager) {
		for (IntervalBase interval : intervalPersister.readIntervals()) {
			interval.setClosed();
			intervals.add(interval);
		}
		intervals.addAll(intervalManager.getOpenIntervals());
	}

	/** Filters out and removes intervals which are older than one hour. */
	private void filterIntervals() {
		ArrayList<IntervalBase> filteredIntervals = new ArrayList<IntervalBase>();
		ArrayList<IntervalBase> intervalsToRemove = new ArrayList<IntervalBase>();

		if (intervals.size() == 0) {
			return;
		}

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
		userTest = aggregateDurations(
				getEditorIntervalsOfDocType(DocumentType.TEST))
				.plus(aggregateDurations(getEditorIntervalsOfDocType(DocumentType.TEST_FRAMEWORK)))
				.plus(aggregateDurations(getEditorIntervalsOfDocType(DocumentType.FILENAME_TEST)))
				.plus(aggregateDurations(getEditorIntervalsOfDocType(DocumentType.PATHNAMME_TEST)));
		userProduction = aggregateDurations(getEditorIntervalsOfDocType(DocumentType.PRODUCTION));
		performDataSanitation();
		perspectiveDebug = aggregateDurations(getPerspectiveIntervalsOfType(Perspective.DEBUG));
		perspectiveJava = aggregateDurations(getPerspectiveIntervalsOfType(Perspective.JAVA));
		perspectiveOther = aggregateDurations(getPerspectiveIntervalsOfType(Perspective.OTHER));
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
