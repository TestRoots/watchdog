package nl.tudelft.watchdog.logic.ui;

import java.util.Date;

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

import nl.tudelft.watchdog.core.logic.interval.IDEIntervalManagerBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.DebugInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.UserActiveInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.WatchDogViewInterval;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.core.logic.ui.InActivityNotifiers;
import nl.tudelft.watchdog.core.logic.ui.InactivityNotifier;
import nl.tudelft.watchdog.core.logic.ui.UserInactivityNotifier;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEventType;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.logic.InitializationManager;
import nl.tudelft.watchdog.eclipse.logic.interval.IntervalManager;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Tests the {@link WatchDogEventManager}. Because this creates the intervals that are
 * eventually transfered to the server, this is one of the most crucial parts of
 * WatchDog. Tests could flicker because they deal with timers (and Java gives
 * no guarantee as to when these timers will be executed).
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WatchDogGlobals.class)

public class WatchDogEventManagerTest {

	private static final int USER_ACTIVITY_TIMEOUT = 300;
	private static final int TIMEOUT_GRACE_PERIOD = (int) (USER_ACTIVITY_TIMEOUT * 1.1);
	private IDEIntervalManagerBase intervalManager;
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
		IDEIntervalManagerBase intervalManagerReal = new IntervalManager(
				Mockito.mock(PersisterBase.class),
				Mockito.mock(PersisterBase.class));
		intervalManager = Mockito.spy(intervalManagerReal);
		mockedTextEditor = Mockito.mock(ITextEditor.class);
		PowerMockito.mockStatic(WatchDogGlobals.class);
		Mockito.when(WatchDogGlobals.getLogDirectory()).thenReturn("watchdog/logs/");
		Mockito.when(WatchDogGlobals.getPreferences()).thenReturn(mockedPreferences);
		Mockito.when(WatchDogGlobals.getUserInactivityTimeoutDuration()).thenReturn(USER_ACTIVITY_TIMEOUT);
		Mockito.when(mockedPreferences.isAuthenticationEnabled()).thenReturn(true);
		Mockito.when(mockedPreferences.isLoggingEnabled()).thenReturn(false);

		WatchDogEventType.intervalManager = intervalManager;
		WatchDogEventType.editorSpecificImplementation = new InitializationManager.EclipseWatchDogEventSpecificImplementation(intervalManager);
		InActivityNotifiers.READING.updateNotifier(new InactivityNotifier(USER_ACTIVITY_TIMEOUT, WatchDogEventType.READING_INACTIVITY));
		InActivityNotifiers.USER_INACTIVITY.updateNotifier(new UserInactivityNotifier(USER_ACTIVITY_TIMEOUT, WatchDogEventType.USER_INACTIVITY));
		InActivityNotifiers.TYPING.updateNotifier(new InactivityNotifier(USER_ACTIVITY_TIMEOUT, WatchDogEventType.TYPING_INACTIVITY));
	}

	@Test
	public void testCreateReadInterval() {
		createMockEvent(WatchDogEventType.ACTIVE_FOCUS);
		Mockito.verify(intervalManager).addInterval(Mockito.isA(ReadingInterval.class));
	}

	@Test
	public void testCreateReadIntervalOnlyOnce() {
		createMockEvent(WatchDogEventType.ACTIVE_FOCUS);
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
		createMockEvent(WatchDogEventType.CARET_MOVED);
		createMockEvent(WatchDogEventType.CARET_MOVED);
		createMockEvent(WatchDogEventType.PAINT);
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
	}

	@Test
	public void testCreateUserActivityIntervalOnlyOnce() {
		createMockEvent(WatchDogEventType.USER_ACTIVITY);
		WatchDogUtils.sleep(50);
		createMockEvent(WatchDogEventType.USER_ACTIVITY);
		WatchDogUtils.sleep(50);
		createMockEvent(WatchDogEventType.USER_ACTIVITY);
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(IntervalBase.class));
	}

	@Test
	public void testReadIntervalIsClosed() {
		createMockEvent(WatchDogEventType.ACTIVE_FOCUS);
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(ReadingInterval.class));
		createMockEvent(WatchDogEventType.INACTIVE_FOCUS);
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(ReadingInterval.class), Mockito.isA(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testCreateWriteInterval() {
		createMockEvent(WatchDogEventType.SUBSEQUENT_EDIT);
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(TypingInterval.class));
	}

	@Test
	public void testCreateWriteIntervalAndNotAReadInterval() {
		createMockEvent(WatchDogEventType.START_EDIT);
		createMockEvent(WatchDogEventType.SUBSEQUENT_EDIT);
		Mockito.verify(intervalManager, Mockito.atLeast(1)).addInterval(
				Mockito.isA(TypingInterval.class));
		Mockito.verify(intervalManager, Mockito.never()).addInterval(
				Mockito.isA(ReadingInterval.class));
		createMockEvent(WatchDogEventType.CARET_MOVED);
		createMockEvent(WatchDogEventType.SUBSEQUENT_EDIT);
		createMockEvent(WatchDogEventType.PAINT);
		Mockito.verify(intervalManager, Mockito.atLeast(1)).addInterval(
				Mockito.isA(TypingInterval.class));
		Mockito.verify(intervalManager, Mockito.never()).addInterval(
				Mockito.isA(ReadingInterval.class));
	}

	@Test
	public void testWritingIntervalsGetClosedOnHigherCancel() {
		createMockEvent(WatchDogEventType.SUBSEQUENT_EDIT);
		createMockEvent(WatchDogEventType.END_IDE);
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(TypingInterval.class), Mockito.isA(Date.class));
	}

	@Test
	public void testTimeoutWorksForRegularIntervals() {
		createMockEvent(WatchDogEventType.ACTIVE_WINDOW);
		createMockEvent(WatchDogEventType.USER_ACTIVITY);
		Mockito.verify(intervalManager, Mockito.timeout(TIMEOUT_GRACE_PERIOD))
				.closeInterval(Mockito.isA(IntervalBase.class),
						Mockito.any(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testTimeoutWorksForReadingIntervals() {
		createMockEvent(WatchDogEventType.ACTIVE_FOCUS);
		Mockito.verify(intervalManager,
				Mockito.timeout(TIMEOUT_GRACE_PERIOD).atLeast(1))
				.closeInterval(Mockito.isA(ReadingInterval.class),
						Mockito.any(Date.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testTimeoutWorksForWritingIntervals() {
		createMockEvent(WatchDogEventType.SUBSEQUENT_EDIT);
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
		createMockEvent(WatchDogEventType.ACTIVE_FOCUS);
		Mockito.verify(intervalManager,
				Mockito.timeout(USER_ACTIVITY_TIMEOUT / 2).never())
				.closeInterval(Mockito.any(IntervalBase.class),
						Mockito.isA(Date.class));
		createMockEvent(WatchDogEventType.CARET_MOVED);
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
		createMockEvent(WatchDogEventType.USER_ACTIVITY);
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 5);
		createMockEvent(WatchDogEventType.ACTIVE_FOCUS);
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 2);
		editorInterval = intervalManager.getEditorInterval();
		interval = intervalManager.getInterval(UserActiveInterval.class);

		createMockEvent(WatchDogEventType.CARET_MOVED);
		WatchDogUtils.sleep(USER_ACTIVITY_TIMEOUT / 2);
		createMockEvent(WatchDogEventType.CARET_MOVED);

		Assert.assertFalse(editorInterval.isClosed());
		Assert.assertFalse(interval.isClosed());
		createMockEvent(WatchDogEventType.USER_ACTIVITY);
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
		createMockEvent(WatchDogEventType.SUBSEQUENT_EDIT);
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
		createMockEvent(WatchDogEventType.START_WATCHDOGVIEW);
		Mockito.verify(intervalManager).addInterval(
				Mockito.isA(WatchDogViewInterval.class));
		createMockEvent(WatchDogEventType.END_WATCHDOGVIEW);
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(WatchDogViewInterval.class), Mockito.isA(Date.class));

	}
	
	@Test
	public void testCreateDebugInterval() {
		createMockEvent(WatchDogEventType.START_DEBUG);
		Mockito.verify(intervalManager).addInterval(Mockito.isA(DebugInterval.class));
		createMockEvent(WatchDogEventType.END_DEBUG);
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.isA(DebugInterval.class), Mockito.isA(Date.class));
	}

	private void createMockEvent(WatchDogEventType watchDogEventType) {
		if (watchDogEventType == WatchDogEventType.SUBSEQUENT_EDIT) {
			watchDogEventType.process(new WatchDogEventType.EditorWithModCount(mockedTextEditor, 0));
		} else {
			watchDogEventType.process(mockedTextEditor);
		}
	}

}
