package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntervalPersisterTest {

	public static final String path = "test.mapdb";

	@Before
	@After
	public void cleanup() {

	}

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

		List<IntervalBase> readIntervals = persister.readIntevals(new Date(0),
				new Date(Long.MAX_VALUE));
		assertEquals(readIntervals.size(), items);

		// Test order of returned results
		for (int i = 1; i < items - 1; i++) {
			assertTrue(readIntervals.get(i - 1).getStart().getTime() < readIntervals
					.get(i).getStart().getTime());
		}

		Collections.sort(generatedIntervals, new Comparator<IntervalBase>() {

			@Override
			public int compare(IntervalBase a, IntervalBase b) {
				if (a.getStart().getTime() > b.getStart().getTime())
					return -1;
				if (a.getStart().getTime() < b.getStart().getTime())
					return 1;
				return 0;
			}

		});

		long median = generatedIntervals.get(items / 2).getStart().getTime();
		readIntervals = persister.readIntevals(new Date(median), new Date(
				Long.MAX_VALUE));
		assertEquals(readIntervals.size(), (items / 2) + 1);
	}

	private List<IntervalBase> generateIntervalList(int n) {

		List<IntervalBase> intervals = new ArrayList<IntervalBase>();
		for (int i = 0; i < n; i++) {
			intervals.add(createRandomInterval());
		}
		return intervals;
	}

	private IntervalBase createRandomInterval() {
		SessionInterval interval = new SessionInterval(0);
		interval.setStartTime(new Date(interval.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		interval.setEndTime(new Date(interval.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		return interval;
	}
}
