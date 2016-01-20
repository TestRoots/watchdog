package nl.tudelft.watchdog.logic.interval;

import java.io.IOException;

import org.junit.AfterClass;

import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.logic.storage.PersisterTestBase;

public abstract class IntervalPersisterTestBase extends PersisterTestBase {
	protected static IntervalPersisterBase persister;

	/**
	 * Initializes the variables required for the tests and initializes the
	 * persister as an IntervalPersister.
	 */
	protected static void setUpSuperClass() {
		PersisterTestBase.setUpSuperClass("IntervalPersisterTests");
		persister = new IntervalPersisterBase(copiedDatabase);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
	}
}
