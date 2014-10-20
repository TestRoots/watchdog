package nl.tudelft.watchdog.ui.infoDialog;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.IntervalManagerBase;
import nl.tudelft.watchdog.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/** Gathers and calculates statistics on interval length. */
public class IntervalStatistics extends IntervalManagerBase {

	private Duration eclipseOpen;
	private Duration userActive;
	private Duration userReading;
	private Duration userTyping;
	private IntervalPersister intervalPersister;
	private Duration userProduction;
	private Duration userTesting;

	/** Constructor. */
	public IntervalStatistics(IntervalPersister intervalPersister) {
		this.intervalPersister = intervalPersister;
		intervals.addAll(intervalPersister.readIntervals());
		filterIntervals();
		calculateStatistics();
	}

	private void calculateStatistics() {
		eclipseOpen = aggregateDurations(IntervalType.ECLIPSE_OPEN);
		userActive = aggregateDurations(IntervalType.USER_ACTIVE);
		userReading = aggregateDurations(IntervalType.READING);
		userTyping = aggregateDurations(IntervalType.TYPING);
		userTesting = aggregateDurations(DocumentType.TEST);
		userProduction = aggregateDurations(DocumentType.PRODUCTION);
	}

	/** Filters out and removes intervals which are older than one hour. */
	private void filterIntervals() {
		ArrayList<IntervalBase> filteredIntervals = new ArrayList<IntervalBase>();
		ArrayList<IntervalBase> intervalsToRemove = new ArrayList<IntervalBase>();

		Date mostRecentDate = intervals.get(intervals.size() - 1).getEnd();
		DateTime thresholdDate = new DateTime(mostRecentDate);
		thresholdDate = thresholdDate.minusHours(1);

		for (IntervalBase interval : intervals) {
			if (interval.getEnd().before(thresholdDate.toDate())) {
				intervalsToRemove.add(interval);
			} else {
				filteredIntervals.add(interval);
			}
		}

		intervalPersister.removeIntervals(intervalsToRemove);
		intervals = filteredIntervals;
	}

	private Duration aggregateDurations(DocumentType type) {
		Duration aggregatedDuration = new Duration(0);
		for (IntervalBase interval : getEditorIntervalsWithDocType(type)) {
			aggregatedDuration = aggregatedDuration
					.plus(interval.getDuration());
		}
		return aggregatedDuration;
	}

	private Duration aggregateDurations(IntervalType type) {
		Duration aggregatedDuration = new Duration(0);
		for (IntervalBase interval : getIntervalsOfType(type)) {
			aggregatedDuration = aggregatedDuration
					.plus(interval.getDuration());
		}
		return aggregatedDuration;
	}
}
