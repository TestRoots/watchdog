package nl.tudelft.watchdog.logic.ui;

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
		inactivityNotifier = new InactivityNotifier(eventManagerMock, TIMEOUT);
	}

	@Test
	public void testTimerNotStartedWhenCreated() {
		Mockito.verifyZeroInteractions(eventManagerMock);
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerExecutedAfterOneTrigger() {
		inactivityNotifier.triggerActivity();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT*2)).update(
				Mockito.any());
	}

	@Test
	public void testTimerExecutedTwice() {
		inactivityNotifier.triggerActivity();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				Mockito.any());
		inactivityNotifier.triggerActivity();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				Mockito.any());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerNotExecutedTooEarly() {
		inactivityNotifier.triggerActivity();
		Mockito.verify(eventManagerMock, Mockito.timeout(HALF_TIMEOUT).never())
				.update(Mockito.any());
		inactivityNotifier.triggerActivity();
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT*2)).update(
				Mockito.any());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerStoppedAfterCancel() {
		inactivityNotifier.triggerActivity();
		Mockito.verify(eventManagerMock, Mockito.timeout(HALF_TIMEOUT).never())
				.update(Mockito.any());
		inactivityNotifier.cancelTimer();
		Mockito.verify(eventManagerMock, Mockito.times(1))
				.update(Mockito.any());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerExecutedRegularly() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			inactivityNotifier.triggerActivity();
			Thread.sleep(HALF_TIMEOUT);
		}
		Mockito.verifyZeroInteractions(eventManagerMock);
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				Mockito.any());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

}
