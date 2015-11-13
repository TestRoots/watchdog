package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.eclipse.logic.ui.EventManager;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.UserActiveInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.WatchDogViewInterval;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent.EventType;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests the {@link EventManager}. Because this creates the intervals that are
 * eventually transfered to the server, this is one of the most crucial parts of
 * WatchDog. Tests could flicker because they deal with timers (and Java gives
 * no guarantee as to when these timers will be executed).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WatchDogGlobals.class)

public class EventManagerTest {

	private static final int USER_ACTIVITY_TIMEOUT = 300;
	private static final int TIMEOUT_GRACE_PERIOD = (int) (USER_ACTIVITY_TIMEOUT * 1.1);
	private EventManager eventManager;
	private IntervalManager intervalManager;
	private ITextEditor mockedTextEditor;
	private EditorIntervalBase editorInterval;
	private IntervalBase interval;
	
	@Mock
	Preferences mockedPreferences;
	
	@Mock
	WatchDogGlobals mockedGlobals;	

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		IntervalManager intervalManagerReal = new IntervalManager(
				Mockito.mock(IntervalPersisterBase.class),
				Mockito.mock(IntervalPersisterBase.class));
		intervalManager = Mockito.spy(intervalManagerReal);
		mockedTextEditor = Mockito.mock(ITextEditor.class);
		eventManager = new EventManager(intervalManager, USER_ACTIVITY_TIMEOUT);
		PowerMockito.mockStatic(WatchDogGlobals.class);
		Mockito.when(WatchDogGlobals.getLogDirectory()).thenReturn("watchdog/logs/");
		Mockito.when(WatchDogGlobals.getPreferences()).thenReturn(mockedPreferences);
		Mockito.when(mockedPreferences.isAuthenticationEnabled()).thenReturn(
				true);
		Mockito.when(mockedPreferences.isLoggingEnabled()).thenReturn(false);
	}

	@Test
	public void testCreateReadInterval() {
		eventManager.update(createMockEvent(EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
	}

	@Test
	public void testCreateReadIntervalOnlyOnce() {
		eventManager.update(createMockEvent(EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
		eventManager.update(createMockEvent(EventType.CARET_MOVED));
		eventManager.update(createMockEvent(EventType.CARET_MOVED));
		eventManager.update(createMockEvent(EventType.PAINT));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
	}

	@Test
	public void testCreateUserActivityIntervalOnlyOnce() {
		eventManager.update(createMockEvent(EventType.USER_ACTIVITY));
		WatchDogUtils.sleep(50);
		eventManager.update(createMockEvent(EventType.USER_ACTIVITY));
		WatchDogUtils.sleep(50);
		eventManager.update(createMockEvent(EventType.USER_ACTIVITY));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(IntervalBase.class));
	}

	@Test
	public void testReadIntervalIsClosed() {
		eventManager.update(createMockEvent(EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
		eventManager.update(createMockEvent(EventType.INACTIVE_FOCUS));
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(ReadingInterval.class), Mockito.isA(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testCreateWriteInterval() {
		eventManager.update(createMockEvent(EventType.SUBSEQUENT_EDIT));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(TypingInterval.class));
	}

	@Test
	public void testCreateWriteIntervalAndNotAReadInterval() {
		eventManager.update(createMockEvent(EventType.START_EDIT));
		eventManager.update(createMockEvent(EventType.SUBSEQUENT_EDIT));
		Mockito.verify(intervalManager, Mockito.atLeast(1)).addInterval(
				Mockito.isA(TypingInterval.class));
		Mockito.verify(intervalManager, Mockito.never()).addInterval(
				Mockito.isA(ReadingInterval.class));
		eventManager.update(createMockEvent(EventType.CARET_MOVED));
		eventManager.update(createMockEvent(EventType.SUBSEQUENT_EDIT));
		eventManager.update(createMockEvent(EventType.PAINT));
		Mockito.verify(intervalManager, Mockito.atLeast(1)).addInterval(
				Mockito.isA(TypingInterval.class));
		Mockito.verify(intervalManager, Mockito.never()).addInterval(
				Mockito.isA(ReadingInterval.class));
	}

	@Test
	public void testWritingIntervalsGetClosedOnHigherCancel() {
		eventManager.update(createMockEvent(EventType.SUBSEQUENT_EDIT));
		eventManager.update(createMockEvent(EventType.END_IDE));
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(TypingInterval.class), Mockito.isA(Date.class));
	}

	@Test
	public void testTimeoutWorksForRegularIntervals() {
		eventManager.update(createMockEvent(EventType.ACTIVE_WINDOW));
		eventManager.update(createMockEvent(EventType.USER_ACTIVITY));
		Mockito.verify(intervalManager, Mockito.timeout(TIMEOUT_GRACE_PERIOD))
				.closeInterval(Mockito.isA(IntervalBase.class),
						Mockito.any(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testTimeoutWorksForReadingIntervals() {
		eventManager.update(createMockEvent(EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager,
				Mockito.timeout(TIMEOUT_GRACE_PERIOD).atLeast(1))
				.closeInterval(Mockito.isA(ReadingInterval.class),
						Mockito.any(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testTimeoutWorksForWritingIntervals() {
		eventManager.update(createMockEvent(EventType.SUBSEQUENT_EDIT));
		// first close null interval
		Mockito.verify(intervalManager,
				Mockito.timeout(TIMEOUT_GRACE_PERIOD).atLeast(1))
				.closeInterval((IntervalBase) Mockito.isNull(),
						Mockito.any(Date.class));

		Mockito.verify(intervalManager,
				Mockito.timeout(TIMEOUT_GRACE_PERIOD).atLeast(1))
				.closeInterval(Mockito.isA(TypingInterval.class),
						Mockito.any(Date.class));
	}

	@Test
	public void testReadingTimeoutIsProlonged() {
		eventManager.update(createMockEvent(EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager,
				Mockito.timeout(USER_ACTIVITY_TIMEOUT / 2).never())
				.closeInterval(Mockito.any(IntervalBase.class),
						Mockito.isA(Date.class));
		eventManager.update(createMockEvent(EventType.CARET_MOVED));
		Mockito.verify(intervalManager,
				Mockito.timeout(TIMEOUT_GRACE_PERIOD).never()).closeInterval(
				Mockito.any(IntervalBase.class), Mockito.isA(Date.class));
		Mockito.verify(intervalManager,
				Mockito.timeout(TIMEOUT_GRACE_PERIOD * 2).atLeast(1))
				.closeInterval(Mockito.isA(ReadingInterval.class),
						Mockito.any(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testNoMoreAdditionalUserActivitiesShouldNotCloseReading() {
		eventManager.update(createMockEvent(EventType.USER_ACTIVITY));
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 5);
		eventManager.update(createMockEvent(EventType.ACTIVE_FOCUS));
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 2);
		editorInterval = intervalManager.getEditorInterval();
		interval = intervalManager.getInterval(UserActiveInterval.class);

		eventManager.update(createMockEvent(EventType.CARET_MOVED));
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 2);
		eventManager.update(createMockEvent(EventType.CARET_MOVED));

		Assert.assertFalse(editorInterval.isClosed());
		Assert.assertFalse(interval.isClosed());
		eventManager.update(createMockEvent(EventType.USER_ACTIVITY));
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 2);

		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT * 3);
		Assert.assertEquals(null, intervalManager.getEditorInterval());
		Assert.assertEquals(null,
				intervalManager.getInterval(UserActiveInterval.class));
		Assert.assertTrue(editorInterval.isClosed());
		Assert.assertTrue(interval.isClosed());
	}

	/**
	 * Advanced synchronization test, which tests whether sub-sequent intervals
	 * (stopped because another interval was stopped) have the same end time
	 * stamp. These intervals used to have a slightly delayed end timestamp.
	 * This test should document that this is fixed.
	 */
	@Test
	public void testEndTimeStampSetAccuratelyForWritingIntervals() {
		testNoMoreAdditionalUserActivitiesShouldNotCloseReading();
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT);
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT);

		Assert.assertTrue(editorInterval.getEnd().getTime() <= interval
				.getEnd().getTime());
	}

	/**
	 * Advanced synchronization test, which certifies that intervals that should
	 * be created independent of each other have indeed different timestamps.
	 */
	@Test
	public void testStartTimeStampShouldDifferForDifferentlyStartedIntervals() {
		testNoMoreAdditionalUserActivitiesShouldNotCloseReading();

		Assert.assertTrue(interval.getStart().before(editorInterval.getStart()));
	}

	/**
	 * This test verifies that one additional {@link IntervalBase} interval is
	 * created when a writing interval is created. This should be of type
	 * {@link IntervalType#USER_ACTIVE}.
	 */
	@Test
	public void testAUserActivityIntervalIsCreatedThroughAnEdit() {
		eventManager.update(createMockEvent(EventType.SUBSEQUENT_EDIT));
		WatchDogUtils.sleep(TIMEOUT_GRACE_PERIOD / 5);
		editorInterval = intervalManager.getEditorInterval();
		interval = intervalManager.getInterval(UserActiveInterval.class);
		Assert.assertNotNull(interval);
	}

	/**
	 * Advanced synchronization test, which tests whether sub-sequent intervals
	 * (started because of another interval was created) have the same starting
	 * time stamp. These intervals used to have a slightly delayed timestamp.
	 * This test should document that this is fixed.
	 */
	@Test
	public void testStartTimeStampSetAccuratelyForWritingIntervals() {
		testAUserActivityIntervalIsCreatedThroughAnEdit();

		Assert.assertEquals(editorInterval.getStart(), interval.getStart());
	}

	@Test
	public void testCreateWatchDogViewInterval() {
		eventManager.update(createMockEvent(EventType.START_WATCHDOGVIEW));
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(WatchDogViewInterval.class));
		eventManager.update(createMockEvent(EventType.END_WATCHDOGVIEW));
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(WatchDogViewInterval.class), Mockito.isA(Date.class));

	}
	

	private WatchDogEvent createMockEvent(EventType eventType) {
		return new WatchDogEvent(mockedTextEditor, eventType);
	}

}
