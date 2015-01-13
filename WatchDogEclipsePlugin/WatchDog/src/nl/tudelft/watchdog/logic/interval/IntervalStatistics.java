package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EclipseOpenInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.JUnitInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActiveInterval;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/** Gathers and calculates statistics on interval length. */
@SuppressWarnings("javadoc")
public class IntervalStatistics extends IntervalManagerBase {
	// Intervals are stored in Database for 10 hours (=600 minutes)
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
		MINUTES_10(0, 10, "10 minutes."), MINUTES_30(1, 30, "30 minutes."), HOUR_1(
				2, 60, "1 hour."), HOURS_2(3, 120, "2 hours."), HOURS_5(4, 300,
				"5 hours."), HOURS_8(5, 480, "8 hours."), HOURS_10(6, 600,
				"10 hours.");
		public final int id;
		public final int numberOfMinutes;
		private final String name;

		StatisticsInterval(int id, int minutes, String name) {
			this.id = id;
			this.numberOfMinutes = minutes;
			this.name = name;
		}

		public static String[] names() {
			String[] result = new String[values().length];
			for (StatisticsInterval interval : values()) {
				result[interval.id] = interval.name;
			}
			return result;
		}
	}

	private StatisticsInterval selectedInterval;

	/** Constructor. */
	public IntervalStatistics(IntervalManager intervalManager,
			StatisticsInterval selectedInterval) {
		intervalPersister = intervalManager.getIntervalsStatisticsPersister();
		this.selectedInterval = selectedInterval;
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
		thresholdDateView = thresholdDateView
				.minusMinutes(selectedInterval.numberOfMinutes);

		for (IntervalBase interval : intervals) {
			// Filtering intervals from the Database
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
				// Removing intervals from the Database
				intervalsToRemove.add(interval);
			}
		}

		oldestDate = filteredIntervals.get(0).getStart();
		intervalPersister.removeIntervals(intervalsToRemove);
		intervals = filteredIntervals;
	}

	private void calculateStatistics() {
		eclipseOpen = aggregateDurations(getIntervals(EclipseOpenInterval.class));
		userActive = aggregateDurations(getIntervals(UserActiveInterval.class));
		userReading = aggregateDurations(getIntervals(ReadingInterval.class));
		userTyping = aggregateDurations(getIntervals(TypingInterval.class));
		userTest = aggregateDurations(getEditorIntervals(DocumentType.TEST))
				.plus(aggregateDurations(getEditorIntervals(DocumentType.TEST_FRAMEWORK)))
				.plus(aggregateDurations(getEditorIntervals(DocumentType.FILENAME_TEST)))
				.plus(aggregateDurations(getEditorIntervals(DocumentType.PATHNAMME_TEST)));
		userProduction = aggregateDurations(getEditorIntervals(DocumentType.PRODUCTION));
		performDataSanitation();

		perspectiveDebug = aggregateDurations(getPerspectiveIntervals(Perspective.DEBUG));
		perspectiveJava = aggregateDurations(getPerspectiveIntervals(Perspective.JAVA));
		perspectiveOther = aggregateDurations(getPerspectiveIntervals(Perspective.OTHER));
		averageTestDuration = getPreciseTime(aggregateDurations(getIntervals(JUnitInterval.class)))
				/ getIntervals(JUnitInterval.class).size();

		junitRunsCount = getIntervals(JUnitInterval.class).size();
	}

	/**
	 * @return An {@link ArrayList} of intervals of the specified document type.
	 */
	protected List<EditorIntervalBase> getEditorIntervals(DocumentType type) {
		List<EditorIntervalBase> collectedIntervals = new ArrayList<EditorIntervalBase>();
		for (EditorIntervalBase interval : getIntervals(EditorIntervalBase.class)) {
			if (interval.getDocument().getDocumentType() == type) {
				collectedIntervals.add(interval);
			}
		}

		return collectedIntervals;
	}

	/**
	 * @return An {@link ArrayList} of intervals of the specified Perspective
	 *         type.
	 */
	protected List<PerspectiveInterval> getPerspectiveIntervals(Perspective type) {
		List<PerspectiveInterval> collectedIntervals = new ArrayList<PerspectiveInterval>();
		for (PerspectiveInterval interval : getIntervals(PerspectiveInterval.class)) {
			if (interval.getPerspectiveType() == type) {
				collectedIntervals.add(interval);
			}
		}
		return collectedIntervals;
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

	private Duration aggregateDurations(List<? extends IntervalBase> intervals) {
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
