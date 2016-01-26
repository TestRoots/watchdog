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
import nl.tudelft.watchdog.core.logic.event.EventManager;
import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointAddEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointChangeEvent;
import nl.tudelft.watchdog.core.logic.event.eventtypes.BreakpointRemoveEvent;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

@RunWith(PowerMockRunner.class)
@PrepareForTest(WatchDogGlobals.class)
public class EventManagerTest {

	private EventManager eventManager;
	private EventPersisterBase eventsToTransferPersister;
	private EventPersisterBase eventsStatisticsPersister;

	@Mock
	Preferences mockedPreferences;

	@Mock
	WatchDogGlobals mockedGlobals;

	@Before
	public void setup() {
		eventsToTransferPersister = Mockito.mock(EventPersisterBase.class);
		eventsStatisticsPersister = Mockito.mock(EventPersisterBase.class);
		eventManager = new EventManager(eventsToTransferPersister, eventsStatisticsPersister);

		PowerMockito.mockStatic(WatchDogGlobals.class);
		Mockito.when(WatchDogGlobals.getLogDirectory()).thenReturn("watchdog/logs/");
		Mockito.when(WatchDogGlobals.getPreferences()).thenReturn(mockedPreferences);
		// Mockito.when(mockedPreferences.isAuthenticationEnabled()).thenReturn(
		// true);
		Mockito.when(mockedPreferences.isLoggingEnabled()).thenReturn(false);
	}

	@Test
	public void testNoInteractionsWhenAddingNull() {
		eventManager.addEvent(null);
		Mockito.verifyZeroInteractions(eventsToTransferPersister);
		Mockito.verifyZeroInteractions(eventsStatisticsPersister);
	}

	@Test
	public void testAddBreakpointAddEvent() {
		BreakpointAddEvent eventReal = new BreakpointAddEvent(1, BreakpointType.LINE, new Date());
		BreakpointAddEvent event = Mockito.spy(eventReal);
		eventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).saveItem(Mockito.isA(BreakpointAddEvent.class));
		Mockito.verify(eventsStatisticsPersister).saveItem(Mockito.isA(BreakpointAddEvent.class));
	}

	@Test
	public void testAddBreakpointRemoveEvent() {
		BreakpointRemoveEvent eventReal = new BreakpointRemoveEvent(1, BreakpointType.LINE, new Date());
		BreakpointRemoveEvent event = Mockito.spy(eventReal);
		eventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).saveItem(Mockito.isA(BreakpointRemoveEvent.class));
		Mockito.verify(eventsStatisticsPersister).saveItem(Mockito.isA(BreakpointRemoveEvent.class));
	}

	@Test
	public void testAddBreakpointChangeEvent() {
		BreakpointChangeEvent eventReal = new BreakpointChangeEvent(1, BreakpointType.LINE, null, new Date());
		BreakpointChangeEvent event = Mockito.spy(eventReal);
		eventManager.addEvent(event);
		Mockito.verify(event).setSessionSeed(Mockito.anyString());
		Mockito.verify(eventsToTransferPersister).saveItem(Mockito.isA(BreakpointChangeEvent.class));
		Mockito.verify(eventsStatisticsPersister).saveItem(Mockito.isA(BreakpointChangeEvent.class));
	}

}
