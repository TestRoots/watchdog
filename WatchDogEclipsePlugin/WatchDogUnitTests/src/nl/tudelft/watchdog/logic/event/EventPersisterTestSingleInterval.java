package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;

public class EventPersisterTestSingleInterval extends EventPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "BaseTest";
		setUpSuperClass();
	}

	private static EventBase event;

	@Test
	public void test1WriteEvent() {
		event = EventPersisterTest.createRandomEvent();
		persister.saveItem(event);

		EventBase savedEvent = new ArrayList<>(persister.readItems()).get(0);
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());
	}

	@Test
	public void test2CompareEventAfterWrite() {
		EventBase savedEvent = new ArrayList<>(persister.readItems()).get(0);
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());
	}

}
