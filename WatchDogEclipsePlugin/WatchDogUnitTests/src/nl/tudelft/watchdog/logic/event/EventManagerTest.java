package nl.tudelft.watchdog.logic.event;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import nl.tudelft.watchdog.core.logic.breakpoint.BreakpointType;
import nl.tudelft.watchdog.core.logic.event.DebugEventManager;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointRemoveEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.DebugEventType;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

/**
 * Tests for testing the correctness of the {@link DebugEventManager} when events are
 * added.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WatchDogGlobals.class)
public class EventManagerTest {

	private DebugEventManager debugEventManager;
	private PersisterBase eventsToTransferPersister;
	private PersisterBase eventsStatisticsPersister;

	@Mock
	Preferences mockedPreferences;

	@Mock
	WatchDogGlobals mockedGlobals;

	@Before
	public void setup() {
		eventsToTransferPersister = Mockito.mock(PersisterBase.class);
		eventsStatisticsPersister = Mockito.mock(PersisterBase.class);
		debugEventManager = new DebugEventManager(eventsToTransferPersister, eventsStatisticsPersister);

		PowerMockito.mockStatic(WatchDogGlobals.class);
		Mockito.when(WatchDogGlobals.getLogDirectory()).thenReturn("watchdog/logs/");
		Mockito.when(WatchDogGlobals.getPreferences()).thenReturn(mockedPreferences);
		Mockito.when(mockedPreferences.isLoggingEnabled()).thenReturn(false);
	}

	@Test
	public void testNoInteractionsWhenAddingNull() {
		debugEventManager.addEvent(null);
		Mockito.verifyZeroInteractions(eventsToTransferPersister);
		Mockito.verifyZeroInteractions(eventsStatisticsPersister);
	}

	@Test
	public void testAddBreakpointAddEvent() {
		BreakpointAddEvent eventReal = new BreakpointAddEvent(1, BreakpointType.LINE, new Date());
		BreakpointAddEvent event = Mockito.spy(eventReal);
		debugEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(BreakpointAddEvent.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(BreakpointAddEvent.class));
	}

	@Test
	public void testAddBreakpointRemoveEvent() {
		BreakpointRemoveEvent eventReal = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date());
		BreakpointRemoveEvent event = Mockito.spy(eventReal);
		debugEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(BreakpointRemoveEvent.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(BreakpointRemoveEvent.class));
	}

	@Test
	public void testAddBreakpointChangeEvent() {
		BreakpointChangeEvent eventReal = new BreakpointChangeEvent(1, BreakpointType.LINE, null, new Date());
		BreakpointChangeEvent event = Mockito.spy(eventReal);
		debugEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(BreakpointChangeEvent.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(BreakpointChangeEvent.class));
	}
	
	@Test
	public void testAddSuspendBreakpointEvent() {
		DebugEventBase eventReal = new DebugEventBase(DebugEventType.SUSPEND_BREAKPOINT, new Date());
		DebugEventBase event = Mockito.spy(eventReal);
		debugEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(DebugEventBase.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(DebugEventBase.class));
	}
	
	@Test
	public void testAddStepIntoEvent() {
		DebugEventBase eventReal = new DebugEventBase(DebugEventType.STEP_INTO, new Date());
		DebugEventBase event = Mockito.spy(eventReal);
		debugEventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).save(Mockito.isA(DebugEventBase.class));
		Mockito.verify(eventsStatisticsPersister).save(Mockito.isA(DebugEventBase.class));
	}

}
