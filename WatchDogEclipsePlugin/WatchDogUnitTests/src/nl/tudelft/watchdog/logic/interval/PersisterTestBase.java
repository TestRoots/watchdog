package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

/**
 * Base class for persistence tests. The convention for this IntervalPersister
 * tests is that directory name equals the database file name.
 * 
 * Subclasses should, instead of using the @BeforeClass annotation, call such
 * classes from within their constructor.
 */
@RunWith(LexicographicalTestOrderRunner.class)
public abstract class PersisterTestBase {
	protected static IntervalPersister persister;

	private static File databaseDirectory;

	protected static File copiedDatabase;

	protected static String databaseName;

	@ClassRule
	public static final TemporaryFolder copiedDirectory = new TemporaryFolder();

	protected static void setUpSuperClass() {
		databaseDirectory = new File(new File("resources",
				"IntervalPersisterTests"), databaseName);
		try {
			FileUtils.copyDirectory(databaseDirectory,
					copiedDirectory.getRoot());
		} catch (IOException e) {
			e.printStackTrace();
		}
		copiedDatabase = new File(copiedDirectory.getRoot(), databaseName
				+ ".map");
		persister = new IntervalPersister(copiedDatabase);
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
	}

	@Test
	public void test0IfDirectoryCopied() {
		assertTrue(databaseDirectory.exists());
		assertTrue(copiedDatabase.exists());
	}
}
