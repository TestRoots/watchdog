package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntervalPersisterInvalidDatabaseTest extends PersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() throws FileNotFoundException,
			UnsupportedEncodingException {
		databaseName = "InvalidTestDB";
		setUpSuperClass();

		PrintWriter writer = new PrintWriter(copiedDatabase, "UTF-8");
		writer.println("Not a MapDB");
		writer.close();
	}

	@Before
	public void setUpBeforeMethod() {
		persister = new IntervalPersister(copiedDatabase);
	}

	@Test
	public void test0DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test1CreateInterval() {
		persister.saveInterval(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
