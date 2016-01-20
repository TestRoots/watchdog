package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;

import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalPersisterBug101Test extends IntervalPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "IntervalPersisterBug101Test";
		setUpSuperClass();
	}
	
	@Test
	public void test1IfDatabaseStartsUpFine() {
		persister = new IntervalPersisterBase(copiedDatabase);
	}

	@Test
	public void test2DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test3CreateInterval() {
		persister.saveItem(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
