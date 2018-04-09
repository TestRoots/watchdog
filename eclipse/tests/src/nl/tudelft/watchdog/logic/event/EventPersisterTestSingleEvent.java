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

	@Test
	public void can_compare_event_after_write() {
		EventBase event = EventPersisterTest.createRandomEvent();
		persister.save(event);

		WatchDogItem item = new ArrayList<>(persister.readItems()).get(0);
		assertTrue(item instanceof EventBase);

		EventBase savedEvent = (EventBase) item;
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());

		WatchDogItem otherItem = new ArrayList<>(persister.readItems()).get(0);
		assertTrue(item instanceof EventBase);

		EventBase savedOtherEvent = (EventBase) otherItem;
		assertEquals(event.getType(), savedOtherEvent.getType());
		assertEquals(event.getTimestamp(), savedOtherEvent.getTimestamp());
	}

}
