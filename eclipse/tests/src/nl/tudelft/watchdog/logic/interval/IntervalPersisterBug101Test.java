package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

public class IntervalPersisterBug101Test extends IntervalPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "IntervalPersisterBug101Test";
		setUpSuperClass();
	}

	@Test
	public void test1IfDatabaseStartsUpFine() {
		persister = new PersisterBase(copiedDatabase);
	}

	@Test
	public void test2DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test3CreateInterval() {
		persister.save(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
