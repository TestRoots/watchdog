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
	private static final int FILTERED_INTERVALS_IN_MINUTES = 600;

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
	public double averageTestDuration;

	public Date mostRecentDate;
	public Date oldestDate;

	public int junitRunsCount;

	/** Different length intervals for statistics display */
	public enum StatisticsInterval {
		MINUTES_10(0), MINUTES_30(1), HOUR_1(2), HOURS_2(3), HOURS_5(4), HOURS_8(
				5), HOURS_10(6);
		public final int id;

		StatisticsInterval(int id) {
			this.id = id;
		}
	}

	private StatisticsInterval selectedInterval;

	/** Constructor. */
	public IntervalStatistics(IntervalManager intervalManager,
			StatisticsInterval selectedInterval) {
		this.selectedInterval = selectedInterval;
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

	/**
	 * Filters out and removes intervals which are older than 10 hours from
	 * Database. Filters intervals for selected time span.
	 */
	private void filterIntervals() {
		int numberOfMinutes = 600;

		switch (selectedInterval) {
		case MINUTES_10:
			numberOfMinutes = 10;
			break;

		case MINUTES_30:
			numberOfMinutes = 30;
			break;

		case HOUR_1:
			numberOfMinutes = 60;
			break;

		case HOURS_2:
			numberOfMinutes = 120;
			break;

		case HOURS_5:
			numberOfMinutes = 300;
			break;

		case HOURS_8:
			numberOfMinutes = 480;
			break;

		case HOURS_10:
			numberOfMinutes = 600;
		}

		ArrayList<IntervalBase> filteredIntervals = new ArrayList<IntervalBase>();
		ArrayList<IntervalBase> intervalsToRemove = new ArrayList<IntervalBase>();

		if (intervals.size() == 0) {
			return;
		}

		mostRecentDate = intervals.get(intervals.size() - 1).getEnd();
		DateTime thresholdDateDatabase = new DateTime(mostRecentDate);
		thresholdDateDatabase = thresholdDateDatabase
				.minusMinutes(FILTERED_INTERVALS_IN_MINUTES);

		DateTime thresholdDateView = new DateTime(mostRecentDate);
		thresholdDateView = thresholdDateView.minusMinutes(numberOfMinutes);

		for (IntervalBase interval : intervals) {
			// Filtering intervals from the last 10 hours
			if (interval.getEnd().after(thresholdDateDatabase.toDate())) {
				// Filtering intervals for selected time span
				if (interval.getEnd().after(thresholdDateView.toDate())) {
					IntervalBase clonedInterval = (IntervalBase) interval
							.clone();
					if (interval.getStart().before(thresholdDateView.toDate())) {
						clonedInterval.setStartTime(thresholdDateView.toDate());
					}
					if (!clonedInterval.isClosed()) {
						clonedInterval.setEndTime(mostRecentDate);
					}
					filteredIntervals.add(clonedInterval);
				}
			} else {
				// Remove from Database intervals older than 10 hours
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
		averageTestDuration = getPreciseTime(aggregateDurations(getIntervalsOfType(IntervalType.JUNIT)))
				/ getIntervalsOfType(IntervalType.JUNIT).size();

		junitRunsCount = getIntervalsOfType(IntervalType.JUNIT).size();
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
