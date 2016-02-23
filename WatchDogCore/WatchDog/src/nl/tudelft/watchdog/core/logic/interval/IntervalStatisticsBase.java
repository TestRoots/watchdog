package nl.tudelft.watchdog.core.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import nl.tudelft.watchdog.core.logic.document.DocumentType;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IDEOpenInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.PerspectiveInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.PerspectiveInterval.Perspective;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.UserActiveInterval;
import nl.tudelft.watchdog.core.logic.network.WatchDogTransferable;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

/**
 * Gathers and calculates statistics on developer behavior in a variable time
 * frame.
 */
@SuppressWarnings("javadoc")
public abstract class IntervalStatisticsBase extends IntervalManagerBase {
	// Intervals are stored in the database for 10 hours (equals 600 minutes)
	private static final int FILTERED_INTERVALS_IN_MINUTES = 600;

	private final PersisterBase intervalPersister;

	public Duration ideOpen;
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

	public int junitSuccessfulRunsCount;
	public int junitFailedRunsCount;
	public int junitRunsCount;

	/** Pre-defined time periods for statistics display. */
	public enum StatisticsTimePeriod {
		MINUTES_10(10, "10 minutes."), MINUTES_30(30, "30 minutes."), HOUR_1(60, "1 hour."), HOURS_2(120,
				"2 hours."), HOURS_5(300, "5 hours."), HOURS_8(480, "8 hours."), HOURS_10(600, "10 hours.");

		private final int minutes;
		private final String name;

		private StatisticsTimePeriod(int minutes, String name) {
			this.minutes = minutes;
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		/** Returns an array with the names of this enum. */
		public static String[] names() {
			StatisticsTimePeriod[] values = values();
			String names[] = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				names[i] = values[i].toString();
			}
			return names;
		}
	}

	private StatisticsTimePeriod selectedInterval;

	/** Constructor. */
	public IntervalStatisticsBase(IDEIntervalManagerBase intervalManager, StatisticsTimePeriod selectedInterval) {
		this.intervalPersister = intervalManager.getIntervalsStatisticsPersister();
		this.selectedInterval = selectedInterval;
		addIntervals(intervalManager);
		filterIntervals();
		calculateStatistics();
	}

	private void addIntervals(IDEIntervalManagerBase intervalManager) {
		for (WatchDogTransferable item : intervalPersister.readItems()) {
			if (item instanceof IntervalBase) {
				IntervalBase interval = (IntervalBase) item;
				interval.setClosed();
				intervals.add(interval);
			}
		}
		intervals.addAll(intervalManager.getOpenIntervals());
	}

	/**
	 * Filters out and removes intervals which are older than 10 hours from
	 * Database. Filters intervals for selected time span.
	 */
	private void filterIntervals() {
		ArrayList<IntervalBase> filteredIntervals = new ArrayList<IntervalBase>();
		ArrayList<WatchDogTransferable> intervalsToRemove = new ArrayList<WatchDogTransferable>();

		if (intervals.size() == 0) {
			return;
		}

		mostRecentDate = intervals.get(intervals.size() - 1).getEnd();
		DateTime thresholdDateDatabase = new DateTime(mostRecentDate);
		thresholdDateDatabase = thresholdDateDatabase.minusMinutes(FILTERED_INTERVALS_IN_MINUTES);

		DateTime thresholdDateView = new DateTime(mostRecentDate);
		thresholdDateView = thresholdDateView.minusMinutes(selectedInterval.minutes);

		for (IntervalBase interval : intervals) {
			if (intervalIsOlderThanThreshold(thresholdDateDatabase, interval)) {
				intervalsToRemove.add(interval);
				continue;
			}

			if (!intervalIsOlderThanThreshold(thresholdDateView, interval)) {
				IntervalBase clonedInterval = null;
				try {
					clonedInterval = (IntervalBase) interval.clone();
					adjustIntervalStartAndEndDate(thresholdDateView, interval, clonedInterval);
					filteredIntervals.add(clonedInterval);
				} catch (CloneNotSupportedException exception) {
					// intentionally empty
				}
			}
		}

		oldestDate = filteredIntervals.get(0).getStart();
		intervalPersister.removeItems(intervalsToRemove);
		intervals = filteredIntervals;
	}

	private void adjustIntervalStartAndEndDate(DateTime thresholdDateView, IntervalBase interval,
			IntervalBase clonedInterval) {
		if (interval.getStart().before(thresholdDateView.toDate())) {
			// Set start of the interval to threshold if the start is
			// before the threshold.
			clonedInterval.setStartTime(thresholdDateView.toDate());
		}
		if (!clonedInterval.isClosed()) {
			clonedInterval.setEndTime(mostRecentDate);
		}
	}

	private boolean intervalIsOlderThanThreshold(DateTime thresholdDateDatabase, IntervalBase interval) {
		return interval.getEnd().before(thresholdDateDatabase.toDate());
	}

	protected void calculateStatistics() {
		ideOpen = aggregateDurations(getIntervals(IDEOpenInterval.class));
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

		calculateJUnitStatistics();
	}

	protected abstract void calculateJUnitStatistics();

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
		if (ideOpen.isShorterThan(userActive)) {
			ideOpen = userActive;
		}
	}

	protected Duration aggregateDurations(List<? extends IntervalBase> intervals) {
		Duration aggregatedDuration = new Duration(0);
		for (IntervalBase interval : intervals) {
			aggregatedDuration = aggregatedDuration.plus(interval.getDuration());
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
