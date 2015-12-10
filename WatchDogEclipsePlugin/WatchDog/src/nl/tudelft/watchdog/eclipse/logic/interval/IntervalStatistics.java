package nl.tudelft.watchdog.eclipse.logic.interval;

import java.util.List;

import nl.tudelft.watchdog.core.logic.interval.IntervalManagerBase;
import nl.tudelft.watchdog.core.logic.interval.IntervalStatisticsBase;
import nl.tudelft.watchdog.eclipse.logic.interval.intervaltypes.JUnitInterval;

public class IntervalStatistics extends IntervalStatisticsBase {

	// TODO: update constructor
	public IntervalStatistics(IntervalManagerBase intervalManager,
			StatisticsTimePeriod selectedInterval) {
		super(intervalManager, selectedInterval);
	}

	@Override
	protected void calculateJUnitStatistics() {
		List<JUnitInterval> junitIntervals = getIntervals(JUnitInterval.class);
		junitRunsCount = junitIntervals.size();
		averageTestDuration = getPreciseTime(aggregateDurations(junitIntervals))
				/ junitRunsCount;
		junitSuccessfulRunsCount = 0;
		junitFailedRunsCount = 0;

		for (JUnitInterval jUnitInterval : junitIntervals) {
			switch (jUnitInterval.getExecutionResult()) {
			case OK:
				junitSuccessfulRunsCount++;
				break;
			case FAILURE:
				junitFailedRunsCount++;
			}
		}
	}

}
