package nl.tudelft.watchdog.logic.event;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointChangeType;
import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointRemoveEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;
import nl.tudelft.watchdog.logic.network.JsonConverterTestBase;

/**
 * Test the transfer from {@link EventBase}s to JSon.
 */
public class EventJsonConverterTest extends JsonConverterTestBase{

	private JsonTransferer transferer = new JsonTransferer();

	@Test
	public void line_breakpoint_added() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.LINE, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"li\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void exception_breakpoint_added() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.EXCEPTION, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"ex\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void field_breakpoint_added() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.FIELD, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"fi\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void method_breakpoint_added() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.METHOD, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"me\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void class_breakpoint_added() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.CLASS, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"cp\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void undefined_breakpoint_added() {
		BreakpointAddEvent event = new BreakpointAddEvent(1, BreakpointType.UNDEFINED, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"un\",\"et\":\"ba\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void line_breakpoint_removed() {
		BreakpointRemoveEvent event = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"li\",\"et\":\"br\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void line_breakpoint_changes() {
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, null, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void breakpoint_unknown_changes() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.UNKNOWN);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"un\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void breakpoint_single_change() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.ENABLED);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"en\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void breakpoint_two_changes() {
		List<BreakpointChangeType> changes = new ArrayList<>();
		changes.add(BreakpointChangeType.ENABLED);
		changes.add(BreakpointChangeType.HC_ADDED);
		BreakpointChangeEvent event = new BreakpointChangeEvent(1, BreakpointType.LINE, changes, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"ch\":[\"en\",\"ha\"],\"bh\":1,\"bt\":\"li\",\"et\":\"bc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void breakpoint_three_changes() {
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
	public void suspend_breakpoint() {
		DebugEventBase event = new DebugEventBase(TrackingEventType.SUSPEND_BREAKPOINT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"et\":\"sb\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void suspend_client() {
		DebugEventBase event = new DebugEventBase(TrackingEventType.SUSPEND_CLIENT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"et\":\"sc\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void step_out() {
		DebugEventBase event = new DebugEventBase(TrackingEventType.STEP_OUT, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"et\":\"st\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void step_into() {
		DebugEventBase event = new DebugEventBase(TrackingEventType.STEP_INTO, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"et\":\"si\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void step_over() {
		DebugEventBase event = new DebugEventBase(TrackingEventType.STEP_OVER, new Date());
		ArrayList<WatchDogItem> events = createSampleEvents(event);

		assertEquals("[{\"et\":\"so\",\"ts\":1,\"ss\":\"\"," + pasteWDVAndClient() + "}]",
				transferer.toJson(events));
	}

	@Test
	public void resume_client() {
		DebugEventBase event = new DebugEventBase(TrackingEventType.RESUME_CLIENT, new Date());
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
