package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;

import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalPersisterTestSingleInterval extends PersisterTestBase {

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
		persister.saveInterval(interval);

		IntervalBase savedInterval = new ArrayList<>(persister.readIntervals())
				.get(0);
		assertEquals(interval.getType(), savedInterval.getType());
		assertEquals(interval.getStart(), savedInterval.getStart());
		assertEquals(interval.getEnd(), savedInterval.getEnd());
		assertEquals(interval.getDuration(), savedInterval.getDuration());
		assertEquals(interval.isClosed(), savedInterval.isClosed());
	}

	@Test
	public void test2CompareIntervalAfterWrite() {
		IntervalBase savedInterval = new ArrayList<>(persister.readIntervals())
				.get(0);
		assertEquals(interval.getType(), savedInterval.getType());
		assertEquals(interval.getStart(), savedInterval.getStart());
		assertEquals(interval.getEnd(), savedInterval.getEnd());
	}

	@Test
	public void test3CompareIntervalAfterWriteDemonstratesCloseIsNotPersisted() {
		IntervalBase savedInterval = new ArrayList<>(persister.readIntervals())
				.get(0);
		assertEquals(interval.getDuration(), savedInterval.getDuration());
		assertEquals(interval.isClosed(), savedInterval.isClosed());
	}

}
