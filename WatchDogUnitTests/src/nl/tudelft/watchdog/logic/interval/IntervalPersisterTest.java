package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;

import org.junit.Test;

public class IntervalPersisterTest {

	public static final String path = "test.mapdb";

	@Test
	public void testInteraction100() {
		testInteraction(100);
	}

	public void testInteraction(int items) {
		IntervalPersister persister = new IntervalPersister(path);
		List<IntervalBase> generatedIntervals = generateIntervalList(items);

		// Shuffle the generated intervals to test for
		// correct ordering of returned values
		Collections.shuffle(generatedIntervals);

		persister.saveIntervals(generatedIntervals);

		List<IntervalBase> readIntervals = persister.readIntevals(0,
				Long.MAX_VALUE);
		assertEquals(readIntervals.size(), items);

		// Test order of returned results
		assertEquals(readIntervals, generatedIntervals);
	}

	private List<IntervalBase> generateIntervalList(int n) {

		List<IntervalBase> intervals = new ArrayList<IntervalBase>();
		for (int i = 0; i < n; i++) {
			intervals.add(createRandomInterval());
		}
		return intervals;
	}

	private IntervalBase createRandomInterval() {
		SessionInterval interval = new SessionInterval("123", 0);
		interval.setStartTime(new Date(interval.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		interval.setEndTime(new Date(interval.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		return interval;
	}
}
