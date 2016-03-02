package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointRemoveEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;

/**
 * Test class that tests the {@link PersisterBase} in case the same or
 * similar events are saved to ensure each different event is added, but
 * duplicate events are not.
 */
public class EventPersisterDuplicatesTest extends EventPersisterTestBase {

	private EventBase event;

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "DuplicateTest";
		setUpSuperClass();
	}

	@Test
	public void test1AddEvent() {
		event = createEvent();
		persister.save(event);
		
		WatchDogItem item = new ArrayList<>(persister.readItems()).get(0);
		assertTrue(item instanceof EventBase);

		EventBase savedEvent = (EventBase) item;
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());
	}

	@Test
	public void test2DatabasePersisted() {
		assertEquals(1, persister.getSize());
	}

	@Test
	public void test3AddSameEventTestNotPersisted() {
		event = createEvent();
		persister.save(event);
		assertEquals(1, persister.getSize());
	}

	@Test
	public void test4AddSimilarEventDifferentTimestampTestPersisted() {
		event = createEvent();
		event.setTimestamp(new Date(2));
		persister.save(event);
		assertEquals(2, persister.getSize());
	}

	@Test
	public void test5AddSimilarEventDifferentTypeTestPersisted() {
		event = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date(1));
		event.setSessionSeed("444");
		persister.save(event);
		assertEquals(3, persister.getSize());
	}

	@Test
	public void test6AddAlreadyPersistedEventAgainTestNotPersisted() {
		event = createEvent();
		persister.save(event);
		assertEquals(3, persister.getSize());
	}

	@Test
	public void test7DatabaseCleared() {
		persister.clearAndResetMap();
		assertEquals(0, persister.getSize());
	}

	private static EventBase createEvent() {
		EventBase event = new BreakpointAddEvent(1, BreakpointType.LINE, new Date(1));
		event.setSessionSeed("444");
		return event;
	}
}
