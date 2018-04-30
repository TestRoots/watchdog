package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import nl.tudelft.watchdog.core.logic.ui.InactivityNotifier;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventTypeInterface;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Tests for inactivity notifier. These tests have the potential to flicker, as
 * they deal with precise timing.
 */
public class InactivityNotifierTest {

	private static final int TIMEOUT = 100;

	private static final int HALF_TIMEOUT = (int) 0.5 * TIMEOUT;

	private WatchDogEventTypeInterface watchdogEventTypeMock;
	private InactivityNotifier inactivityNotifier;

	@Before
	public void setup() {
		watchdogEventTypeMock = Mockito.mock(WatchDogEventTypeInterface.class);
		inactivityNotifier = new InactivityNotifier(TIMEOUT, watchdogEventTypeMock);
	}

	@Test
	public void timer_not_wstarted_when_created() {
		Mockito.verifyZeroInteractions(watchdogEventTypeMock);
		Mockito.verifyNoMoreInteractions(watchdogEventTypeMock);
	}

	@Test
	public void timer_executed_after_one_trigger() {
		inactivityNotifier.trigger();
		Mockito.verify(watchdogEventTypeMock, Mockito.timeout(TIMEOUT * 2)).process(Mockito.any());
	}

	@Test
	public void timer_executed_twice() {
		inactivityNotifier.trigger();
		Mockito.verify(watchdogEventTypeMock, Mockito.timeout(TIMEOUT * 2)).process(Mockito.any());
		inactivityNotifier.trigger();
		Mockito.verify(watchdogEventTypeMock, Mockito.timeout(TIMEOUT * 2)).process(Mockito.any());
		Mockito.verifyNoMoreInteractions(watchdogEventTypeMock);
	}

	@Test
	public void timer_not_executed_too_early() {
		inactivityNotifier.trigger();
		inactivityNotifier.trigger();
		Mockito.verify(watchdogEventTypeMock, Mockito.timeout(TIMEOUT * 2)).process(Mockito.any());
		Mockito.verifyNoMoreInteractions(watchdogEventTypeMock);
	}

	@Test
	public void timer_stopped_after_cancel() {
		inactivityNotifier.trigger();
		Date cancelDate = new Date();
		inactivityNotifier.cancelTimer(cancelDate);
		Mockito.verify(watchdogEventTypeMock, Mockito.times(1)).process(Mockito.eq(cancelDate), Mockito.any());
		Mockito.verifyNoMoreInteractions(watchdogEventTypeMock);
	}

	@Test
	public void timer_executed_in_regular_interval() {
		for (int i = 0; i < 5; i++) {
			inactivityNotifier.trigger();
			WatchDogUtils.sleep(HALF_TIMEOUT);
		}
		Mockito.verifyZeroInteractions(watchdogEventTypeMock);
		Mockito.verify(watchdogEventTypeMock, Mockito.timeout(TIMEOUT * 2)).process(Mockito.any());
		Mockito.verifyNoMoreInteractions(watchdogEventTypeMock);
	}

}
