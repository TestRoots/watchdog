package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntervalPersisterTestSingleInterval {

	private IntervalPersister persister;

	private static IntervalBase interval;

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
	
	@Ignore
	@Test
	public void test3CompareIntervalAfterWriteDemonstratesCloseIsNotPersisted() {
		IntervalBase savedInterval = new ArrayList<>(persister.readIntervals())
				.get(0);
		assertEquals(interval.getDuration(), savedInterval.getDuration());
		assertEquals(interval.isClosed(), savedInterval.isClosed());
	}
	

}
