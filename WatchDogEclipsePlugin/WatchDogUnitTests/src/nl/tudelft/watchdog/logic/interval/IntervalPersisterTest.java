package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IDEOpenInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;

import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalPersisterTest extends PersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "BaseTest";
		setUpSuperClass();
	}

	@Test
	public void test1Interaction100() {
		testInteraction(100);
	}

	private void testInteraction(int items) {
		List<IntervalBase> generatedIntervals = generateIntervalList(items);

		// Shuffle the generated intervals to test for
		// correct ordering of returned values
		Collections.shuffle(generatedIntervals);
		Collections.sort(generatedIntervals);

		for (IntervalBase intervalBase : generatedIntervals) {
			persister.saveItem(intervalBase);
		}

		List<IntervalBase> readIntervals = new ArrayList<IntervalBase>(
				persister.readItems());
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

	public static IntervalBase createRandomInterval() {
		IntervalBase interval = new IDEOpenInterval(new Date());
		interval.setSessionSeed("444");
		interval.setStartTime(new Date(interval.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		interval.setEndTime(new Date(interval.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		return interval;
	}

	@Test
	public void test2DatabasePersisted() {
		assertEquals(100, persister.getSize());
	}

	@Test
	public void test3RemoveFirstInterval() {
		assertEquals(100, persister.getSize());
		Iterator<IntervalBase> readIntervals = persister.readItems()
				.iterator();
		ArrayList<IntervalBase> firstInterval = new ArrayList<IntervalBase>(
				Arrays.asList(readIntervals.next()));
		persister.removeItems(firstInterval);
		assertEquals(99, persister.getSize());
	}

	@Test
	public void test4DatabaseCleared() {
		persister.clearAndResetMap();
		assertEquals(0, persister.getSize());
	}

}
