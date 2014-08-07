package nl.tudelft.watchdog.logic.eclipseuireader.events;

import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StartEditingEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StopEditingEditorEvent;
import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.interval.intervaltypes.ReadingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActivityIntervalBase;
import nl.tudelft.watchdog.util.WatchDogGlobals;

/**
 * Observer for {@link EditorEvent}s. Links such events to actions in the
 * IntervalManager.
 */
public class UserActionManager {
	/** The {@link IntervalManager} this observer is working with. */
	private IntervalManager intervalManager;

	/** Constructor. */
	public UserActionManager(IntervalManager intervalManager) {
		this.intervalManager = intervalManager;
	}

	/**
	 * Introduces the supplied editorEvent
	 */
	public void update(EditorEvent editorEvent) {
		System.out.println("Event " + editorEvent);
		// TODO (MMB) double events may fire!
		boolean previousIntervalHasSameEditor = false;

		UserActivityIntervalBase userActivityInterval = intervalManager
				.getUserActivityIntervalIfAny();

		if (userActivityInterval != null) {
			if (userActivityInterval.getEditor().equals(
					editorEvent.getTextEditor())) {
				previousIntervalHasSameEditor = true;
			}
		}

		if (editorEvent instanceof StartEditingEditorEvent) {
			if (previousIntervalHasSameEditor
					&& userActivityInterval instanceof TypingInterval) {
				// in case we already have a typing interval do nothing
				return;
			}
			intervalManager.closeInterval(userActivityInterval);
			createNewActiveTypingInterval(editorEvent);
		} else if (editorEvent instanceof StopEditingEditorEvent) {
			intervalManager.closeInterval(userActivityInterval);
		} else if (editorEvent instanceof FocusStartEditorEvent) {
			if (previousIntervalHasSameEditor
					&& userActivityInterval instanceof ReadingInterval) {
				if (userActivityInterval.isClosed()) {
					intervalManager.closeInterval(userActivityInterval);
					createNewActiveReadingInterval(editorEvent);
				} else {
					// in case we already have a reading interval do nothing
					// TODO (MMB) I think in this case, we need to prolong the
					// timeout time
					return;
				}
			}
			intervalManager.closeInterval(userActivityInterval);
			createNewActiveReadingInterval(editorEvent);
		} else if (editorEvent instanceof FocusEndEditorEvent) {
			intervalManager.closeInterval(userActivityInterval);
		}
	}

	/** Creates a new active typing interval from the supplied event. */
	private void createNewActiveTypingInterval(EditorEvent event) {
		intervalManager.addAndSetNewActiveInterval(
				new TypingInterval(event.getPart(), intervalManager
						.getSessionSeed()), WatchDogGlobals.TYPING_TIMEOUT);
	}

	/** Creates a new active reading interval from the supplied event. */
	private void createNewActiveReadingInterval(EditorEvent event) {
		intervalManager.addAndSetNewActiveInterval(
				new ReadingInterval(event.getPart(), intervalManager
						.getSessionSeed()), WatchDogGlobals.READING_TIMEOUT);
	}
}