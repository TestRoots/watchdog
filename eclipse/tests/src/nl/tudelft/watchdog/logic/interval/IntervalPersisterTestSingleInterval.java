package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalPersisterTestSingleInterval extends IntervalPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "BaseTest";
		setUpSuperClass();
	}

	private static IntervalBase interval;

	@Test
	public void test1WriteInterval() {
		interval = IntervalPersisterTest.createRandomInterval();
		interval.close();
		persister.save(interval);

		WatchDogItem savedItem = new ArrayList<>(persister.readItems())
				.get(0);
		assertTrue(savedItem instanceof IntervalBase);

		IntervalBase savedInterval = (IntervalBase) savedItem;
		assertEquals(interval.getType(), savedInterval.getType());
		assertEquals(interval.getStart(), savedInterval.getStart());
		assertEquals(interval.getEnd(), savedInterval.getEnd());
		assertEquals(interval.getDuration(), savedInterval.getDuration());
		assertEquals(interval.isClosed(), savedInterval.isClosed());
	}

	@Test
	public void test2CompareIntervalAfterWrite() {
		WatchDogItem savedItem = new ArrayList<>(persister.readItems())
				.get(0);
		assertTrue(savedItem instanceof IntervalBase);

		IntervalBase savedInterval = (IntervalBase) savedItem;
		assertEquals(interval.getType(), savedInterval.getType());
		assertEquals(interval.getStart(), savedInterval.getStart());
		assertEquals(interval.getEnd(), savedInterval.getEnd());
	}

	@Test
	public void test3CompareIntervalAfterWriteDemonstratesCloseIsNotPersisted() {
		WatchDogItem savedItem = new ArrayList<>(persister.readItems())
				.get(0);
		assertTrue(savedItem instanceof IntervalBase);

		IntervalBase savedInterval = (IntervalBase) savedItem;
		assertEquals(interval.getDuration(), savedInterval.getDuration());
		assertEquals(interval.isClosed(), savedInterval.isClosed());
	}

}
