package nl.tudelft.watchdog.logic.interval;

import java.util.Observable;
import java.util.Observer;

import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StartEditingEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StopEditingEditorEvent;
import nl.tudelft.watchdog.logic.interval.active.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.TypingInterval;
import nl.tudelft.watchdog.logic.interval.active.UserActivityIntervalBase;
import nl.tudelft.watchdog.util.WatchDogGlobals;

/**
 * Observer for {@link EditorEvent}s. Links such events to actions in the
 * IntervalManager.
 */
/* package */class EditorEventObserver implements Observer {
	/** The {@link IntervalManager} this observer is working with. */
	private IntervalManager intervalManager;

	/** Constructor. */
	EditorEventObserver(IntervalManager intervalManager) {
		this.intervalManager = intervalManager;
	}

	@Override
	public void update(Observable observable, Object event) {
		if (!(event instanceof EditorEvent)) {
			return;
		}
		EditorEvent editorEvent = (EditorEvent) event;
		UserActivityIntervalBase userActivityInterval = intervalManager
				.getUserActivityIntervalIfAny();
		if (event instanceof StartEditingEditorEvent) {
			// create a new active interval when document is new
			if (userActivityInterval == null || userActivityInterval.isClosed()) {
				createNewActiveTypingInterval(editorEvent);
			} else if (userActivityInterval.getEditor() != editorEvent
					.getTextEditor()) {
				intervalManager.closeInterval(userActivityInterval);
				createNewActiveTypingInterval(editorEvent);
			}
		} else if (editorEvent instanceof StopEditingEditorEvent) {
			if (userActivityInterval != null
					&& editorEvent.getTextEditor() == userActivityInterval
							.getEditor()) {
				intervalManager.closeInterval(userActivityInterval);
			}
		} else if (editorEvent instanceof FocusStartEditorEvent) {
			// create a new active interval when document is new
			if (userActivityInterval == null || userActivityInterval.isClosed()) {
				createNewActiveReadingInterval(editorEvent);
			} else if (userActivityInterval.getEditor() != editorEvent
					.getTextEditor()) {
				intervalManager.closeInterval(userActivityInterval);
				createNewActiveReadingInterval(editorEvent);
			}
		} else if (editorEvent instanceof FocusEndEditorEvent) {
			if (userActivityInterval != null
					&& editorEvent.getTextEditor() == userActivityInterval
							.getEditor()) {
				intervalManager.closeInterval(userActivityInterval);
			}
		}
	}

	/** Creates a new active typing interval from the supplied event. */
	private void createNewActiveTypingInterval(EditorEvent event) {
		intervalManager.createNewInterval(new TypingInterval(event.getPart()),
				WatchDogGlobals.TYPING_TIMEOUT);
	}

	/** Creates a new active reading interval from the supplied event. */
	private void createNewActiveReadingInterval(EditorEvent event) {
		intervalManager.createNewInterval(new ReadingInterval(event.getPart()),
				WatchDogGlobals.READING_TIMEOUT);
	}
}