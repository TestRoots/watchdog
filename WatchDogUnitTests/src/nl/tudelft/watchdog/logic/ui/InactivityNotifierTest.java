package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.logic.ui.events.WatchDogEvent.EventType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for inactivity notifier. These tests have the potential to flicker, as
 * they deal with precise timing.
 */
public class InactivityNotifierTest {

	private static final int TIMEOUT = 100;

	private static final int HALF_TIMEOUT = (int) 0.5 * TIMEOUT;

	private EventManager eventManagerMock;
	private InactivityNotifier inactivityNotifier;

	@Before
	public void setup() {
		eventManagerMock = Mockito.mock(EventManager.class);
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
	public void testTimerExecutedRegularly() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			inactivityNotifier.trigger();
			Thread.sleep(HALF_TIMEOUT);
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
