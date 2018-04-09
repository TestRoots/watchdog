package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

public class IntervalPersisterBug101Test extends IntervalPersisterTestBase {

	@BeforeClass
	public static void setup_before_class() {
		databaseName = "IntervalPersisterBug101Test";
		setUpSuperClass();
	}

	@Test
	public void can_insert_into_copied_db() {
		persister = new PersisterBase(copiedDatabase);
		assertEquals(0, persister.getSize());

		persister.save(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
