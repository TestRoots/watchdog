package nl.tudelft.watchdog.logic.interval;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

public class PersisterTestBase {
	protected IntervalPersister persister;

	private static File databaseFile = new File("test.mapdb");

	@BeforeClass
	public static void beforeClass() {
		if (databaseFile.exists() && databaseFile.canWrite()) {
			databaseFile.delete();
		}
	}

	@Before
	public void setUp() {
		persister = new IntervalPersister(databaseFile);
	}

	@After
	public void tearDown() {
		persister.closeDatabase();
	}
}