package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointRemoveEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventType;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;
import nl.tudelft.watchdog.logic.network.JsonConverterTestBase;

/**
 * Test the transfer from {@link EventBase}s to JSon.
 */
public class EventJsonConverterTest extends JsonConverterTestBase{
	
	private JsonTransferer transferer = new JsonTransferer();
	
	@Test
	public void testJsonLineBreakpointAddEventRepresentation() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.LINE, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"li\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonExceptionBreakpointAddEventRepresentation() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.EXCEPTION, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"ex\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonFieldBreakpointAddEventRepresentation() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.FIELD, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"fi\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonMethodBreakpointAddEventRepresentation() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.METHOD, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"me\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonClassBreakpointAddEventRepresentation() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.CLASS, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"cp\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonUndefinedBreakpointAddEventRepresentation() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.UNDEFINED, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"un\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonLineBreakpointRemoveEventRepresentation() {
		BreakpointRemoveEvent event = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"li\",\"et\":\"br\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonLineBreakpointChangeEventNoChanges() {
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, null, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonLineBreakpointChangeEventUnknownChange() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.UNKNOWN);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"un\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonLineBreakpointChangeEventSingleChange() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.ENABLED);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"en\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonLineBreakpointChangeEventTwoChanges() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.ENABLED);
		changes.add(BreakpointChangeType.HC_ADDED);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"en\",\"ha\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonLineBreakpointChangeEventThreeChanges() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.DISABLED);
		changes.add(BreakpointChangeType.COND_DISABLED);
		changes.add(BreakpointChangeType.COND_CHANGED);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"ds\",\"cd\",\"cc\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonSuspendBreakpointEvent() {
		DebugEventBase event = new DebugEventBase(EventType.SUSPEND_BREAKPOINT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);
		
		assertEquals("[{\"et\":\"sb\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonSuspendClientEvent() {
		DebugEventBase event = new DebugEventBase(EventType.SUSPEND_CLIENT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);
		
		assertEquals("[{\"et\":\"sc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonStepOutEvent() {
		DebugEventBase event = new DebugEventBase(EventType.STEP_OUT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);
		
		assertEquals("[{\"et\":\"st\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonStepIntoEvent() {
		DebugEventBase event = new DebugEventBase(EventType.STEP_INTO, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);
		
		assertEquals("[{\"et\":\"si\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonStepOverEvent() {
		DebugEventBase event = new DebugEventBase(EventType.STEP_OVER, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);
		
		assertEquals("[{\"et\":\"so\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}
	
	@Test
	public void testJsonResumeClientEvent() {
		DebugEventBase event = new DebugEventBase(EventType.RESUME_CLIENT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);
		
		assertEquals("[{\"et\":\"rc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	private ArrayList<WatchDogItem> createSampleEvents(EventBase event) {
		ArrayList<WatchDogItem> events = new ArrayList<WatchDogItem>();
		event.setTimestamp(new Date(1));
		event.setSessionSeed("");
		events.add(event);
		return events;
	}
	
}
