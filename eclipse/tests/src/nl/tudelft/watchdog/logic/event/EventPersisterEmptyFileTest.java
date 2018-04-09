package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

public class EventPersisterEmptyFileTest extends EventPersisterTestBase {

	@BeforeClass
	public static void setup_before_class() {
		databaseName = "EmptyTestDB";
		setUpSuperClass();
	}

	/**
	 * A database from an empty file is created before every test execution to
	 * ensure that the database is initialised correctly.
	 */
	@Before
	public void setup() throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(copiedDatabase, "UTF-8");
		writer.println("");
		writer.close();

		persister = new PersisterBase(copiedDatabase);
	}

	@Test
	public void can_insert_into_empty_file_db() {
		assertEquals(0, persister.getSize());

		persister.save(EventPersisterTest.createRandomEvent());
		assertEquals(1, persister.getSize());
	}

}
