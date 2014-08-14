package nl.tudelft.watchdog.logic.ui;

import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent.EventType;

import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests the {@link EventManager}. Because this creates the intervals that are
 * eventually transfered to the server, this is one of the most crucial parts of
 * WatchDog.
 */
public class EventManagerTest {

	private static final int USER_ACTIVITY_TIMEOUT = 300;
	private EventManager eventManager;
	private IntervalManager intervalManager;

	@Before
	public void setup() {
		IntervalManager intervalManagerReal = new IntervalManager(
				Mockito.mock(IntervalPersister.class),
				Mockito.mock(DocumentFactory.class));
		intervalManager = Mockito.spy(intervalManagerReal);
		eventManager = new EventManager(intervalManager, USER_ACTIVITY_TIMEOUT);
	}

	@Test
	public void testCreateReadInterval() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(ReadingInterval.class));
	}

	@Test
	public void testCreateReadIntervalOnlyOnce() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(ReadingInterval.class));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.CARET_MOVED));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.CARET_MOVED));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.PAINT));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(ReadingInterval.class));
	}

	@Test
	public void testReadIntervalIsClosed() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(ReadingInterval.class));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.END_FOCUS));
		Mockito.verify(intervalManager, Mockito.atLeastOnce()).closeInterval(
				Mockito.any(ReadingInterval.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	@Ignore
	public void testCreateWriteInterval() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.EDIT));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(TypingInterval.class));
	}

	@Test
	@Ignore
	public void testCreateWriteIntervalOnlyOnce() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.EDIT));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(ReadingInterval.class));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.CARET_MOVED));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.EDIT));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.PAINT));
		Mockito.verify(intervalManager).addAndSetEditorInterval(
				Mockito.any(TypingInterval.class));
	}

	@Test
	public void testTimeoutWorksForRegularIntervals() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.ACTIVE_WINDOW));
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.ACTIVITY));
		Mockito.verify(intervalManager,
				Mockito.timeout((int) (USER_ACTIVITY_TIMEOUT * 1.1)))
				.closeInterval(Mockito.any(IntervalBase.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}

	@Test
	public void testTimeoutWorksForReadingIntervals() {
		eventManager.update(new WatchDogEvent(Mockito.mock(ITextEditor.class),
				EventType.ACTIVE_FOCUS));
		Mockito.verify(intervalManager,
				Mockito.timeout((int) (USER_ACTIVITY_TIMEOUT * 1.1)))
				.closeInterval(Mockito.any(ReadingInterval.class));
		Assert.assertEquals(null, intervalManager.getEditorInterval());
	}
}
