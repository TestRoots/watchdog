package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Base class for persistence tests. The convention for this IntervalPersister
 * tests is that directory name equals the database file name.
 * 
 * Subclasses should, instead of using the @BeforeClass annotation, call such
 * classes from within their constructor.
 */
public abstract class PersisterTestBase {
	protected static IntervalPersister persister;

	private static File databaseDirectory;

	protected static File copiedDatabase;

	protected static String databaseName;

	private static File copiedDirectory;

	protected static void setUpSuperClass() {
		databaseDirectory = new File(new File("resources",
				"IntervalPersisterTests"), databaseName);
		PersisterTestBase.copiedDirectory = new File(databaseDirectory
				+ "-Temp");
		if (copiedDirectory.exists() && copiedDirectory.canWrite()) {
			copiedDirectory.delete();
		}

		try {
			FileUtils.copyDirectory(databaseDirectory, copiedDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		copiedDatabase = new File(copiedDirectory, databaseName + ".map");
		persister = new IntervalPersister(copiedDatabase);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
		FileUtils.deleteDirectory(copiedDirectory);
	}

	@Test
	public void test0IfDirectoryCopied() {
		assertTrue(databaseDirectory.exists());
		assertTrue(copiedDatabase.exists());
	}
}