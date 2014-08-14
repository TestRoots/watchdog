package nl.tudelft.watchdog.logic.ui;

import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.IntervalPersister;
import nl.tudelft.watchdog.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.logic.ui.WatchDogEvent.EventType;

import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests the {@link EventManager}. Because this creates the intervals that are
 * eventually transfered to the server, this is one of the most crucial parts of
 * WatchDog.
 */
public class EventManagerTest {

	private EventManager eventManager;
	private IntervalManager intervalManager;

	@Before
	public void setup() {
		IntervalManager intervalManagerReal = new IntervalManager(
				Mockito.mock(IntervalPersister.class),
				Mockito.mock(DocumentFactory.class));
		intervalManager = Mockito.spy(intervalManagerReal);
		eventManager = new EventManager(intervalManager);
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

}
