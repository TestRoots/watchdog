package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import nl.tudelft.watchdog.core.logic.ui.InactivityNotifier;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.eclipse.logic.ui.WatchDogEventManager;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Tests for inactivity notifier. These tests have the potential to flicker, as
 * they deal with precise timing.
 */
public class InactivityNotifierTest {

	private static final int TIMEOUT = 100;

	private static final int HALF_TIMEOUT = (int) 0.5 * TIMEOUT;

	private WatchDogEventManager eventManagerMock;
	private InactivityNotifier inactivityNotifier;

	@Before
	public void setup() {
		eventManagerMock = Mockito.mock(WatchDogEventManager.class);
		inactivityNotifier = new InactivityNotifier(eventManagerMock, TIMEOUT,
				EventType.USER_INACTIVITY);
	}

	@Test
	public void testTimerNotStartedWhenCreated() {
		Mockito.verifyZeroInteractions(eventManagerMock);
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerExecutedAfterOneTrigger() {
		inactivityNotifier.trigger();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT * 2)).update(
				createAnyWatchDogEvent());
	}

	@Test
	public void testTimerExecutedTwice() {
		inactivityNotifier.trigger();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				createAnyWatchDogEvent());
		inactivityNotifier.trigger();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				createAnyWatchDogEvent());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerNotExecutedTooEarly() {
		inactivityNotifier.trigger();
		Mockito.verify(eventManagerMock, Mockito.timeout(HALF_TIMEOUT).never())
				.update(createAnyWatchDogEvent());
		inactivityNotifier.trigger();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT * 2)).update(
				createAnyWatchDogEvent());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerStoppedAfterCancel() {
		inactivityNotifier.trigger();
		Mockito.verify(eventManagerMock, Mockito.timeout(HALF_TIMEOUT).never())
				.update(createAnyWatchDogEvent());
		Date cancelDate = new Date();
		inactivityNotifier.cancelTimer(cancelDate);
		Mockito.verify(eventManagerMock, Mockito.times(1)).update(
				createAnyWatchDogEvent(), Mockito.eq(cancelDate));
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerExecutedRegularly() {
		for (int i = 0; i < 5; i++) {
			inactivityNotifier.trigger();
			WatchDogUtils.sleep(HALF_TIMEOUT);
		}
		Mockito.verifyZeroInteractions(eventManagerMock);
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				createAnyWatchDogEvent());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	private WatchDogEvent createAnyWatchDogEvent() {
		return Mockito.isA(WatchDogEvent.class);
	}

}
