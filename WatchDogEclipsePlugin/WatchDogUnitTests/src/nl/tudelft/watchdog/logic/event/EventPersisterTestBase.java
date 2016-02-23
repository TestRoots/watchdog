package nl.tudelft.watchdog.logic.event;

import java.io.IOException;

import org.junit.AfterClass;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.logic.storage.PersisterTestBase;

public abstract class EventPersisterTestBase extends PersisterTestBase {
	protected static PersisterBase persister;

	/**
	 * Initializes the variables required for the tests.
	 */
	protected static void setUpSuperClass() {
		PersisterTestBase.setUpSuperClass("EventPersisterTests");
		persister = new PersisterBase(copiedDatabase);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
	}
}
