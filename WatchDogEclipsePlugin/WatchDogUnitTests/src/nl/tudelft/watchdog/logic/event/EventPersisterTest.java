package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;

public class EventPersisterTest extends EventPersisterTestBase {

	@BeforeClass
	public static void setUpBeforeClass() {
		databaseName = "BaseTest";
		setUpSuperClass();
	}

	@Test
	public void test1Interaction100() {
		testInteraction(100);
	}

	private void testInteraction(int items) {
		List<EventBase> generatedEvents = generateEventList(items);

		// Shuffle the generated events to test for
		// correct ordering of returned values
		Collections.shuffle(generatedEvents);
		Collections.sort(generatedEvents);

		for (EventBase event : generatedEvents) {
			persister.save(event);
		}

		List<EventBase> readEvents = new ArrayList<EventBase>(persister.readItems());
		assertEquals(readEvents.size(), items);

		// Test order of returned results
		assertEquals(readEvents, generatedEvents);
	}

	private List<EventBase> generateEventList(int n) {
		List<EventBase> events = new ArrayList<EventBase>();
		for (int i = 0; i < n; i++) {
			events.add(createRandomEvent());
		}
		return events;
	}

	public static EventBase createRandomEvent() {
		EventBase event = new BreakpointAddEvent(new Random().nextInt(100000), BreakpointType.LINE, new Date());
		event.setSessionSeed("444");
		event.setTimestamp(new Date(event.getTimestamp().getTime() + (new Random()).nextInt(100000)));
		return event;
	}

	@Test
	public void test2DatabasePersisted() {
		assertEquals(100, persister.getSize());
	}

	@Test
	public void test3RemoveFirstEvent() {
		assertEquals(100, persister.getSize());
		Iterator<EventBase> readEvents = persister.readItems().iterator();
		ArrayList<EventBase> firstEvent = new ArrayList<EventBase>(Arrays.asList(readEvents.next()));
		persister.removeItems(firstEvent);
		assertEquals(99, persister.getSize());
	}

	@Test
	public void test4DatabaseCleared() {
		persister.clearAndResetMap();
		assertEquals(0, persister.getSize());
	}

}
