package nl.tudelft.watchdog.logic.interval;

import java.io.IOException;

import org.junit.AfterClass;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.logic.storage.PersisterTestBase;

public abstract class IntervalPersisterTestBase extends PersisterTestBase {
	protected static PersisterBase persister;

	/**
	 * Initializes the variables required for the tests.
	 */
	protected static void setUpSuperClass() {
		PersisterTestBase.setUpSuperClass("IntervalPersisterTests");
		persister = new PersisterBase(copiedDatabase);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
	}
}
