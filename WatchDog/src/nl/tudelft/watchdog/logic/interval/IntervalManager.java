package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.eclipseuireader.events.EventObservable;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StartEditingEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StopEditingEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.ClosingIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.NewIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.listeners.UIListener;
import nl.tudelft.watchdog.logic.interval.active.ActiveIntervalBase;
import nl.tudelft.watchdog.logic.interval.active.ActiveReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.ActiveTypingInterval;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;
import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.util.WatchDogGlobals;
import nl.tudelft.watchdog.util.WatchDogUtils;

/**
 * Manages interval listeners and keeps track of all intervals. Implements the
 * observer pattern, i.e. listeners can subscribe to interval events and will be
 * notified by an implementation of the {@link RecordedIntervalManager}. Is a
 * singleton.
 */
public class IntervalManager {

	/** Notifies subscribers of editorEvents. */
	private EventObservable editorEventObservable;

	/** Notifies subscribers of intervalEvents. */
	private EventObservable intervalEventObservable;

	/** The currently open {@link ActiveReadingInterval}. */
	private ActiveReadingInterval readingInterval;

	/** The currently open {@link ActiveTypingInterval}. */
	private ActiveTypingInterval typingInterval;

	/** The UI listener */
	private UIListener uiListener;

	/** The document factory. */
	private DocumentFactory documentFactory;

	/** The recorded intervals of this session */
	private List<RecordedInterval> recordedIntervals;

	/** The singleton instance of the interval manager. */
	private static IntervalManager instance = null;

	/** Private constructor. */
	private IntervalManager() {
		recordedIntervals = new ArrayList<RecordedInterval>();
		documentFactory = new DocumentFactory();
		editorEventObservable = new EventObservable();
		intervalEventObservable = new EventObservable();
		uiListener = new UIListener();
		addEditorObserversAndUIListeners();
	}

	/**
	 * Returns the existing or creates and returns a new {@link IntervalManager}
	 * instance.
	 */
	public static IntervalManager getInstance() {
		if (instance == null) {
			instance = new IntervalManager();
		}
		return instance;
	}

	/**
	 * Creates change listeners for different document events.
	 */
	private void addEditorObserversAndUIListeners() {
		editorEventObservable.addObserver(new editorEventObserver());
		uiListener.attachListeners();
	}

	/** Closes the current interval (if it is not already closed). */
	private void closeInterval(ActiveIntervalBase interval) {
		if (!interval.isClosed()) {
			Document document = documentFactory.createDocument(interval
					.getPart());
			RecordedInterval recordedInterval = new RecordedInterval(document,
					interval.getTimeOfCreation(), new Date(),
					interval.getActivityType(), WatchDogUtils.isInDebugMode());
			recordedIntervals.add(recordedInterval);
			interval.closeInterval();
			intervalEventObservable.notifyObservers(new ClosingIntervalEvent(
					recordedInterval));
		}
	}

	/** Creates a new editing interval. */
	private void createNewEditingInterval(final EditorEvent evt) {
		typingInterval = new ActiveTypingInterval(evt.getTextEditor());
		addNewIntervalHandler(typingInterval, WatchDogGlobals.TYPING_TIMEOUT);
	}

	/** Creates a new reading interval. */
	private void createNewReadingInterval(final EditorEvent evt) {
		readingInterval = new ActiveReadingInterval(evt.getPart());
		addNewIntervalHandler(readingInterval, WatchDogGlobals.READING_TIMEOUT);
	}

	/**
	 * Adds a new interval handler base, and defines its timeout, i.e. when the
	 * interval is closed.
	 */
	private void addNewIntervalHandler(final ActiveIntervalBase interval,
			int timeout) {
		interval.addTimeoutListener(timeout, new OnInactiveCallBack() {
			@Override
			public void onInactive() {
				closeInterval(interval);
			}
		});
		intervalEventObservable.notifyObservers(new NewIntervalEvent(interval));
	}

	/** Registers a new interval listener. */
	public void addIntervalListener(Observer listener) {
		intervalEventObservable.addObserver(listener);
	}

	/** Removes an existing interval listener. */
	public void removeIntervalListener(Observer listener) {
		intervalEventObservable.deleteObserver(listener);
	}

	/** Returns a list of recorded intervals. */
	public List<RecordedInterval> getRecordedIntervals() {
		return recordedIntervals;
	}

	/** Sets a list of recorded intervals. */
	public void setRecordedIntervals(List<RecordedInterval> intervals) {
		recordedIntervals = intervals;
	}

	/** Closes all currently open intervals. */
	public void closeAllCurrentIntervals() {
		// close editing interval
		if (typingInterval != null) {
			closeInterval(typingInterval);
		}
		// close reading interval
		if (readingInterval != null) {
			closeInterval(readingInterval);
		}
	}

	/**
	 * @return editorEventObservable
	 */
	public EventObservable getEditorObserveable() {
		return editorEventObservable;
	}

	/**
	 * Observer for {@link EditorEvent}s. Links such events to actions in the
	 * IntervalManager.
	 */
	private final class editorEventObserver implements Observer {
		@Override
		public void update(Observable o, Object event) {
			if (!(event instanceof EditorEvent)) {
				return;
			}
			EditorEvent editorEvent = (EditorEvent) event;
			if (event instanceof StartEditingEditorEvent) {
				// create a new active interval when doc is new
				if (typingInterval == null || typingInterval.isClosed()) {
					createNewEditingInterval(editorEvent);
				} else if (typingInterval.getEditor() != editorEvent
						.getTextEditor()) {
					closeInterval(typingInterval);
					createNewEditingInterval(editorEvent);
				}
			} else if (event instanceof StopEditingEditorEvent) {
				if (typingInterval != null
						&& editorEvent.getTextEditor() == typingInterval
								.getEditor()) {
					closeInterval(typingInterval);
				}
			} else if (event instanceof FocusStartEditorEvent) {
				// create a new active interval when doc is new
				if (readingInterval == null || readingInterval.isClosed()) {
					createNewReadingInterval(editorEvent);
				} else if (readingInterval.getEditor() != editorEvent
						.getTextEditor()) {
					closeInterval(readingInterval);
					createNewReadingInterval(editorEvent);
				}
			} else if (event instanceof FocusEndEditorEvent) {
				if (readingInterval != null
						&& editorEvent.getTextEditor() == readingInterval
								.getEditor()) {
					closeInterval(readingInterval);
				}
			}
		}
	}
}
