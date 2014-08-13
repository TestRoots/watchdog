package nl.tudelft.watchdog.logic.ui;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class InactivityNotifierTest {

	private static final int TIMEOUT = 100;

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
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				Mockito.any());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

	@Test
	public void testTimerExecutedRegularly() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			inactivityNotifier.triggerActivity();
			Thread.sleep(TIMEOUT - 10);
		}
		Mockito.verifyZeroInteractions(eventManagerMock);
		Mockito.verify(eventManagerMock, Mockito.timeout(TIMEOUT)).update(
				Mockito.any());
		Mockito.verifyNoMoreInteractions(eventManagerMock);
	}

}
