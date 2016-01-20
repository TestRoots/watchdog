package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;

public class EventPersisterBug101Test extends EventPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "EventPersisterBug101Test";
		setUpSuperClass();
	}

	@Test
	public void test1IfDatabaseStartsUpFine() {
		persister = new EventPersisterBase(copiedDatabase);
	}

	@Test
	public void test2DatabaseEmpty() {
		assertEquals(0, persister.getSize());
	}

	@Test
	public void test3CreateEvent() {
		persister.saveItem(EventPersisterTest.createRandomEvent());
		assertEquals(1, persister.getSize());
	}

}
