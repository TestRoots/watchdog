package nl.tudelft.watchdog.logic.storage;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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

	private static File databaseDirectory;

	protected static File copiedDatabase;

	protected static String databaseName;

	@ClassRule
	public static final TemporaryFolder copiedDirectory = new TemporaryFolder();

	protected static void setUpSuperClass(String childDirName) {
		databaseDirectory = new File(new File("resources", childDirName), databaseName);
		try {
			FileUtils.copyDirectory(databaseDirectory, copiedDirectory.getRoot());
		} catch (IOException e) {
			e.printStackTrace();
		}
		copiedDatabase = new File(copiedDirectory.getRoot(), databaseName + ".mapdb");
	}

	@Test
	public void test0IfDirectoryCopied() {
		assertTrue(databaseDirectory.exists());
		assertTrue(copiedDatabase.exists());
	}
}
