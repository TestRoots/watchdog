package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.eclipseuireader.events.ImmediateNotifyingObservable;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusEndEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.FocusStartEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StartEditingEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.editor.StopEditingEditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.ClosingIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.NewIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.listeners.UIListener;
import nl.tudelft.watchdog.logic.interval.active.ActiveIntervalBase;
import nl.tudelft.watchdog.logic.interval.active.ActiveReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.ActiveTypingInterval;
import nl.tudelft.watchdog.logic.interval.active.ActiveUserActivityIntervalBase;
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

	/** A list of currently opened intervals. */
	private List<ActiveIntervalBase> intervals = new ArrayList<ActiveIntervalBase>();

	/** Notifies subscribers of editorEvents. */
	private ImmediateNotifyingObservable editorEventObservable;

	/** Notifies subscribers of intervalEvents. */
	private ImmediateNotifyingObservable intervalEventObservable;

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
		editorEventObservable = new ImmediateNotifyingObservable();
		intervalEventObservable = new ImmediateNotifyingObservable();
		uiListener = new UIListener(editorEventObservable);
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

	/** Creates change listeners for different document events. */
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
	private void createNewInterval(ActiveIntervalBase interval, int timeout) {
		// TODO (MMB) shouldn't this handler be added to the interval itself?
		intervals.add(interval);
		addNewIntervalHandler(interval, timeout);
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
		for (ActiveIntervalBase interval : intervals) {
			closeInterval(interval);
		}
	}

	/**
	 * @return editorEventObservable
	 */
	public ImmediateNotifyingObservable getEditorObserveable() {
		// TODO (MMB) herein lies the problem - is actually called twice.
		return editorEventObservable;
	}

	/**
	 * @return the single ActivityInterval that is actually a
	 *         UserActivityInterval. There can only be one such interval at any
	 *         given time. If there is none, <code>null</code>.
	 */
	private ActiveIntervalBase getUserActivityIntervalIfAny() {
		for (ActiveIntervalBase interval : intervals) {
			if (interval instanceof ActiveUserActivityIntervalBase) {
				return interval;
			}
		}
		return null;
	}

	/**
	 * Observer for {@link EditorEvent}s. Links such events to actions in the
	 * IntervalManager.
	 */
	private final class editorEventObserver implements Observer {
		@Override
		public void update(Observable observable, Object event) {
			if (!(event instanceof EditorEvent)) {
				return;
			}
			EditorEvent editorEvent = (EditorEvent) event;
			ActiveIntervalBase userActivityInterval = getUserActivityIntervalIfAny();
			if (event instanceof StartEditingEditorEvent) {
				// create a new active interval when document is new
				if (userActivityInterval == null
						|| userActivityInterval.isClosed()) {
					createNewActiveTypingInterval(editorEvent);
				} else if (userActivityInterval.getEditor() != editorEvent
						.getTextEditor()) {
					closeInterval(userActivityInterval);
					createNewActiveTypingInterval(editorEvent);
				}
			} else if (editorEvent instanceof StopEditingEditorEvent) {
				if (userActivityInterval != null
						&& editorEvent.getTextEditor() == userActivityInterval
								.getEditor()) {
					closeInterval(userActivityInterval);
				}
			} else if (editorEvent instanceof FocusStartEditorEvent) {
				// create a new active interval when document is new
				if (userActivityInterval == null
						|| userActivityInterval.isClosed()) {
					createNewActiveReadingInterval(editorEvent);
				} else if (userActivityInterval.getEditor() != editorEvent
						.getTextEditor()) {
					closeInterval(userActivityInterval);
					createNewActiveReadingInterval(editorEvent);
				}
			} else if (editorEvent instanceof FocusEndEditorEvent) {
				if (userActivityInterval != null
						&& editorEvent.getTextEditor() == userActivityInterval
								.getEditor()) {
					closeInterval(userActivityInterval);
				}
			}
		}

		/** Creates a new active typing interval from the supplied event. */
		private void createNewActiveTypingInterval(EditorEvent event) {
			createNewInterval(new ActiveTypingInterval(event.getPart()),
					WatchDogGlobals.TYPING_TIMEOUT);
		}

		/** Creates a new active reading interval from the supplied event. */
		private void createNewActiveReadingInterval(EditorEvent event) {
			createNewInterval(new ActiveReadingInterval(event.getPart()),
					WatchDogGlobals.READING_TIMEOUT);
		}
	}
}
