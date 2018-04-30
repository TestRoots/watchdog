package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

public class IntervalPersisterInvalidDatabaseTest extends IntervalPersisterTestBase {

	@BeforeClass
	public static void setup_before_class() throws FileNotFoundException,
			UnsupportedEncodingException {
		databaseName = "InvalidTestDB";
		setUpSuperClass();

		PrintWriter writer = new PrintWriter(copiedDatabase, "UTF-8");
		writer.println("Not a MapDB");
		writer.close();
	}

	/**
	 * Re-read the entire (saved) database before every single test case again.
	 * This is to make sure that the invalid database gets overriden, hence it
	 * must be re-read after its first opened in {@link #setUpSuperClass()}.
	 */
	@Before
	public void setup() {
		persister = new PersisterBase(copiedDatabase);
	}

	@Test
	public void can_add_to_invalid_db() {
		assertEquals(0, persister.getSize());

		persister.save(IntervalPersisterTest.createRandomInterval());
		assertEquals(1, persister.getSize());
	}

}
