package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.eclipseuireader.events.EditorEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.DocumentNotifier;
import nl.tudelft.watchdog.logic.eclipseuireader.events.IDocumentAttentionListener;
import nl.tudelft.watchdog.logic.eclipseuireader.listeners.UIListener;
import nl.tudelft.watchdog.logic.interval.active.ActiveIntervalBase;
import nl.tudelft.watchdog.logic.interval.active.ActiveReadingInterval;
import nl.tudelft.watchdog.logic.interval.active.ActiveTypingInterval;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;
import nl.tudelft.watchdog.logic.interval.events.ClosingIntervalEvent;
import nl.tudelft.watchdog.logic.interval.events.IIntervalListener;
import nl.tudelft.watchdog.logic.interval.events.IntervalNotifier;
import nl.tudelft.watchdog.logic.interval.events.NewIntervalEvent;
import nl.tudelft.watchdog.logic.interval.recorded.IInterval;
import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.util.WatchDogGlobals;
import nl.tudelft.watchdog.util.WatchDogUtils;

/**
 * Manages interval listeners and keeps track of all intervals. Implements the
 * observer pattern, i.e. listeners can subscribe to interval events and will be
 * notified by an implementation of the {@link IIntervalManager}. Is a
 * singleton.
 */
public class IntervalManager extends IntervalNotifier {

	/** The currently open {@link ActiveReadingInterval}. */
	private ActiveReadingInterval readingInterval;

	/** The currently open {@link ActiveTypingInterval}. */
	private ActiveTypingInterval typingInterval;

	/** The UI listener */
	private UIListener uiListener;

	/** The document factory. */
	private DocumentFactory documentFactory;

	/** The recorded intervals of this session */
	private List<IInterval> recordedIntervals;

	/** The singleton instance of the interval manager. */
	private static IntervalManager instance = null;

	/** Private constructor. */
	private IntervalManager() {
		recordedIntervals = new ArrayList<IInterval>();
		documentFactory = new DocumentFactory();
		registerAndCreateDocumentChangeListeners();
		uiListener = new UIListener();
		uiListener.attachListeners();
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
	private void registerAndCreateDocumentChangeListeners() {
		DocumentNotifier.addMyEventListener(new IDocumentAttentionListener() {

			@Override
			public void onDocumentStartEditing(
					final EditorEvent evt) {
				// create a new active interval when doc is new
				if (typingInterval == null || typingInterval.isClosed()) {
					createNewEditingInterval(evt);
				} else if (typingInterval.getEditor() != evt.getTextEditor()) {
					closeCurrentInterval(typingInterval);
					createNewEditingInterval(evt);
				}
			}

			@Override
			public void onDocumentStopEditing(
					EditorEvent evt) {
				if (typingInterval != null
						&& evt.getTextEditor() == typingInterval.getEditor()) {
					closeCurrentInterval(typingInterval);
				}
			}

			@Override
			public void onDocumentStartFocus(
					EditorEvent evt) {
				// create a new active interval when doc is new
				if (readingInterval == null || readingInterval.isClosed()) {
					createNewReadingInterval(evt);
				} else if (readingInterval.getEditor() != evt
						.getTextEditor()) {
					closeCurrentInterval(readingInterval);
					createNewReadingInterval(evt);
				}
			}

			@Override
			public void onDocumentEndFocus(EditorEvent evt) {
				if (readingInterval != null
						&& evt.getTextEditor() == readingInterval
								.getEditor()) {
					closeCurrentInterval(readingInterval);
				}
			}

		});
	}

	/** Closes the current interval (if it is not already closed). */
	private void closeCurrentInterval(ActiveIntervalBase interval) {
		if (!interval.isClosed()) {
			Document doc = documentFactory.createDocument(interval.getPart());
			RecordedInterval recordedInterval = new RecordedInterval(doc,
					interval.getTimeOfCreation(), new Date(),
					interval.getActivityType(), WatchDogUtils.isInDebugMode());
			recordedIntervals.add(recordedInterval);
			interval.closeInterval();
			IntervalNotifier.fireOnClosingInterval(new ClosingIntervalEvent(
					recordedInterval));
		}
	}

	/** Creates a new editing interval. */
	private void createNewEditingInterval(
			final EditorEvent evt) {
		typingInterval = new ActiveTypingInterval(evt.getTextEditor());
		addNewIntervalHandler(typingInterval, WatchDogGlobals.TYPING_TIMEOUT);
	}

	/** Creates a new reading interval. */
	private void createNewReadingInterval(
			final EditorEvent evt) {
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
				closeCurrentInterval(interval);
			}
		});
		IntervalNotifier.fireOnNewInterval(new NewIntervalEvent(interval));
	}

	/** Registers a new interval listener. */
	public void addIntervalListener(IIntervalListener listener) {
		IntervalNotifier.addMyEventListener(listener);
	}

	/** Removes an existing interval listener. */
	public void removeIntervalListener(IIntervalListener listener) {
		IntervalNotifier.removeMyEventListener(listener);
	}

	/** Returns a list of recorded intervals. */
	public List<IInterval> getRecordedIntervals() {
		return recordedIntervals;
	}

	/** Sets a list of recorded intervals. */
	public void setRecordedIntervals(List<IInterval> intervals) {
		recordedIntervals = intervals;
	}

	/** Closes all currently open intervals. */
	public void closeAllCurrentIntervals() {
		// close editing interval
		if (typingInterval != null) {
			closeCurrentInterval(typingInterval);
		}
		// close reading interval
		if (readingInterval != null) {
			closeCurrentInterval(readingInterval);
		}
	}

}
