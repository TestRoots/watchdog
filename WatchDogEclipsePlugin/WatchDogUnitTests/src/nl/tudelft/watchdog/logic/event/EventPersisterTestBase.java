package nl.tudelft.watchdog.logic.event;

import java.io.IOException;

import org.junit.AfterClass;

import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;
import nl.tudelft.watchdog.logic.storage.PersisterTestBase;

public abstract class EventPersisterTestBase extends PersisterTestBase {
	protected static EventPersisterBase persister;

	/**
	 * Initializes the variables required for the tests and initializes the
	 * persister as an EventPersister.
	 */
	protected static void setUpSuperClass() {
		PersisterTestBase.setUpSuperClass("EventPersisterTests");
		persister = new EventPersisterBase(copiedDatabase);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
	}
}
