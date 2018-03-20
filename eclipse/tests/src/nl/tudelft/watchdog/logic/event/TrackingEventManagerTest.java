package nl.tudelft.watchdog.logic.event;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.TrackingEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.BreakpointRemoveEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.debugging.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.TrackingEventType;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

/**
 * Tests for testing the correctness of the {@link TrackingEventManager} when events are
 * added.
 */
@RunWith(MockitoJUnitRunner.class)
public class TrackingEventManagerTest {

	private TrackingEventManager trackingEventManager;
	private PersisterBase eventsToTransferPersister;
	private PersisterBase eventsStatisticsPersister;

	@Mock
	Preferences mockedPreferences;

	@Before
	public void setup() {
		eventsToTransferPersister = Mockito.mock(PersisterBase.class);
		eventsStatisticsPersister = Mockito.mock(PersisterBase.class);
		trackingEventManager = new TrackingEventManager(eventsToTransferPersister, eventsStatisticsPersister);

		Mockito.when(mockedPreferences.isLoggingEnabled()).thenReturn(false);
	}

	@Test
	public void testNoInteractionsWhenAddingNull() { 
	    trackingEventManager.addEvent(null);
		Mockito.verifyZeroInteractions(eventsToTransferPersister);
		Mockito.verifyZeroInteractions(eventsStatisticsPersister);
	}

	@Test
	public void testAddBreakpointAddEvent() {
		BreakpointAddEvent eventReal = new BreakpointAddEvent(1, BreakpointType.LINE, new Date());
		BreakpointAddEvent event = Mockito.spy(eventReal);
		trackingEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(BreakpointAddEvent.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(BreakpointAddEvent.class));
	}

	@Test
	public void testAddBreakpointRemoveEvent() {
		BreakpointRemoveEvent eventReal = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date());
		BreakpointRemoveEvent event = Mockito.spy(eventReal);
		trackingEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(BreakpointRemoveEvent.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(BreakpointRemoveEvent.class));
	}

	@Test
	public void testAddBreakpointChangeEvent() {
		BreakpointChangeEvent eventReal = new BreakpointChangeEvent(1, BreakpointType.LINE, null, new Date());
		BreakpointChangeEvent event = Mockito.spy(eventReal);
		trackingEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(BreakpointChangeEvent.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(BreakpointChangeEvent.class));
	}
	
	@Test
	public void testAddSuspendBreakpointEvent() {
		DebugEventBase eventReal = new DebugEventBase(TrackingEventType.SUSPEND_BREAKPOINT, new Date());
		DebugEventBase event = Mockito.spy(eventReal);
		trackingEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(DebugEventBase.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(DebugEventBase.class));
	}
	
	@Test
	public void testAddStepIntoEvent() {
		DebugEventBase eventReal = new DebugEventBase(TrackingEventType.STEP_INTO, new Date());
		DebugEventBase event = Mockito.spy(eventReal);
		trackingEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(DebugEventBase.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(DebugEventBase.class));
	}

}
