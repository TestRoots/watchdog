package nl.tudelft.watchdog.logic.interval;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;

import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.logic.storage.PersisterTestBase;

public abstract class IntervalPersisterTestBase extends PersisterTestBase {	
	protected static IntervalPersisterBase persister;
	
	protected static void setUpSuperClass() {
		PersisterTestBase.setUpSuperClass("IntervalPersisterTests");
		createPersister(copiedDatabase);
	}

	protected static void createPersister(final File file) {
		persister = new IntervalPersisterBase(file);
	}
	
	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
	}
}
