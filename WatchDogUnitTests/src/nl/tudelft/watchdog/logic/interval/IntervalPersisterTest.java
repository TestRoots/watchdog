package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntervalPersisterTest {

	private IntervalPersister persister;

	private static File databaseFile = new File("test.mapdb");

	@BeforeClass
	public static void beforeClass() {
		if (databaseFile.exists() && databaseFile.canWrite()) {
			databaseFile.delete();
		}
	}

	@Before
	public void setUp() {
		persister = new IntervalPersister(databaseFile);
	}

	@After
	public void tearDown() {
		persister.closeDatabase();
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
			persister.saveInterval(intervalBase);
		}

		List<IntervalBase> readIntervals = new ArrayList<IntervalBase>(
				persister.readIntervals());
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
		IntervalBase interval = new IntervalBase(IntervalType.ECLIPSE_OPEN,
				new Date());
		interval.setSessionSeed(444);
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
		Iterator<IntervalBase> readIntervals = persister.readIntervals()
				.iterator();
		ArrayList<IntervalBase> firstInterval = new ArrayList<IntervalBase>(
				Arrays.asList(readIntervals.next()));
		persister.removeIntervals(firstInterval);
		assertEquals(99, persister.getSize());
	}

	@Test
	public void test4DatabaseCleared() {
		persister.clearAndResetMap();
		assertEquals(0, persister.getSize());
	}

}
