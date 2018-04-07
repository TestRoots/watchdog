package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

public class EventPersisterTestSingleEvent extends EventPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "BaseTest";
		setUpSuperClass();
	}

	private static EventBase event;

	@Test
	public void test1WriteEvent() {
		event = EventPersisterTest.createRandomEvent();
		persister.save(event);

		WatchDogItem item = new ArrayList<>(persister.readItems()).get(0);
		assertTrue(item instanceof EventBase);

		EventBase savedEvent = (EventBase) item;
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());
	}

	@Test
	public void test2CompareEventAfterWrite() {
		WatchDogItem item = new ArrayList<>(persister.readItems()).get(0);
		assertTrue(item instanceof EventBase);

		EventBase savedEvent = (EventBase) item;
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());
	}

}
