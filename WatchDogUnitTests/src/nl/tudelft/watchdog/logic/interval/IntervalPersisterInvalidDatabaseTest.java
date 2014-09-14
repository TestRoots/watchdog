package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntervalPersisterInvalidDatabaseTest {

	private IntervalPersister persister;

	private static File databaseFile = new File("invalidtest.mapdb");

	@BeforeClass
	public static void beforeClass() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(databaseFile, "UTF-8");
		writer.println("Not a MapDB");
		writer.close();
	}

	@Before
	public void setUp() {
		persister = new IntervalPersister(databaseFile);
	}

	@After
	public void tearDown() {
		persister.closeDatabase();
	}

	@Test
	public void test0DatabaseEmpty() {
		assertEquals(-1, persister.getSize());
	}
	
	@Test
	public void test1CreateInterval() {
		persister.saveInterval(IntervalPersisterTest.createRandomInterval());
		assertEquals(0, persister.getSize());
	}
	
}
