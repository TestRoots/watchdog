package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntervalPersisterBug101Test {

	private static IntervalPersister persister;

	private static File copiedDirectory;

	private static File copiedDatabase;

	@BeforeClass
	public static void setUp() throws IOException {
		File directoryToCopy = new File("resources",
				"IntervalPersisterBug101Test");
		copiedDirectory = new File("resources",
				"IntervalPersisterBug101Test-Temp");
		FileUtils.copyDirectory(directoryToCopy, copiedDirectory);
		copiedDatabase = new File(copiedDirectory, "intervals.mapdb");
	}

	@AfterClass
	public static void tearDown() throws IOException {
		persister.closeDatabase();
		FileUtils.deleteDirectory(copiedDirectory);
	}

	@Test
	public void test0IfDirectoryCopied() {
		assertTrue(copiedDirectory.exists());
		assertTrue(copiedDatabase.exists());
	}

	@Test
	public void test1IfDatabaseStartsUpFine() {
		persister = new IntervalPersister(copiedDatabase);
	}

	@Test
	public void test2DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test3CreateInterval() {
		persister.saveInterval(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
