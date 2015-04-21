package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
