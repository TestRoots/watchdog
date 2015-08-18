package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalPersister;

import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalPersisterBug101Test extends PersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "IntervalPersisterBug101Test";
		setUpSuperClass();
	}
	
	@Test
	public void test1IfDatabaseStartsUpFine() {
		persister = new IntervalPersister(copiedDatabase);
	}

	@Test
	public void test2DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test3CreateInterval() {
		persister.saveInterval(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
