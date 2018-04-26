package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointRemoveEvent;
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
	public static void setup_before_class() {
		databaseName = "DuplicateTest";
		setUpSuperClass();
	}

	@Test
	public void can_handle_duplicates() {
		add_event();
		database_persisted();
		add_same_event_not_persisted_twice();
		similar_event_different_timestamp();
		similar_event_different_sessionseed();
		same_event_not_persisted_twice_again();
		database_cleared();
	}

	private void add_event() {
		event = createEvent();
		persister.save(event);

		WatchDogItem item = new ArrayList<>(persister.readItems()).get(0);
		assertTrue(item instanceof EventBase);

		EventBase savedEvent = (EventBase) item;
		assertEquals(event.getType(), savedEvent.getType());
		assertEquals(event.getTimestamp(), savedEvent.getTimestamp());
	}

	private void database_persisted() {
		assertEquals(1, persister.getSize());
	}

	private void add_same_event_not_persisted_twice() {
		event = createEvent();
		persister.save(event);
		assertEquals(1, persister.getSize());
	}

	private void similar_event_different_timestamp() {
		event = createEvent();
		event.setTimestamp(new Date(2));
		persister.save(event);
		assertEquals(2, persister.getSize());
	}

	private void similar_event_different_sessionseed() {
		event = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date(1));
		event.setSessionSeed("444");
		persister.save(event);
		assertEquals(3, persister.getSize());
	}

	private void same_event_not_persisted_twice_again() {
		event = createEvent();
		persister.save(event);
		assertEquals(3, persister.getSize());
	}

	private void database_cleared() {
		persister.clearAndResetMap();
		assertEquals(0, persister.getSize());
	}

	private static EventBase createEvent() {
		EventBase event = new BreakpointAddEvent(1, BreakpointType.LINE, new Date(1));
		event.setSessionSeed("444");
		return event;
	}
}
