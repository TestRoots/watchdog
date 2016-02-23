package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;

public class EventPersisterInvalidDatabaseTest extends EventPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() throws FileNotFoundException, UnsupportedEncodingException {
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
	public void setUpBeforeMethod() {
		persister = new EventPersisterBase(copiedDatabase);
	}

	@Test
	public void test1DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test2CreateEvent() {
		persister.save(EventPersisterTest.createRandomEvent());
		assertEquals(1, persister.getSize());
	}

}
