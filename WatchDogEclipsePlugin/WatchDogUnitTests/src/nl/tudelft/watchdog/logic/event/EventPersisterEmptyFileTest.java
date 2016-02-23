package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;

public class EventPersisterEmptyFileTest extends EventPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "EmptyTestDB";
		setUpSuperClass();
	}

	/**
	 * A database from an empty file is created before every test execution to
	 * ensure that the database is initialised correctly.
	 */
	@Before
	public void setUpBeforeMethod() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(copiedDatabase, "UTF-8");
		writer.println("");
		writer.close();

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
