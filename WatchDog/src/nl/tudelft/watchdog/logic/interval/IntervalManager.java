package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;

import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.eclipseuireader.events.ImmediateNotifyingObservable;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.ClosingIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.interval.NewIntervalEvent;
import nl.tudelft.watchdog.logic.eclipseuireader.events.listeners.UIListener;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;
import nl.tudelft.watchdog.logic.interval.active.UserActivityIntervalBase;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallback;
import nl.tudelft.watchdog.util.WatchDogUtils;

/**
 * Manages interval listeners and keeps track of all intervals. Implements the
 * observer pattern, i.e. listeners can subscribe to interval events and will be
 * notified by an implementation of the {@link RecordedIntervalManager}. Is a
 * singleton.
 */
public class IntervalManager {

	/** A list of currently opened intervals. */
	private List<IntervalBase> intervals = new ArrayList<IntervalBase>();

	/** Notifies subscribers of editorEvents. */
	private ImmediateNotifyingObservable editorEventObservable;

	/** Notifies subscribers of intervalEvents. */
	private ImmediateNotifyingObservable intervalEventObservable;

	/** The UI listener */
	private UIListener uiListener;

	/** The document factory. */
	private DocumentFactory documentFactory;

	/** The recorded intervals of this session */
	private List<IntervalBase> recordedIntervals;

	/** The singleton instance of the interval manager. */
	private static IntervalManager instance = null;

	/** Private constructor. */
	private IntervalManager() {
		recordedIntervals = new ArrayList<IntervalBase>();
		documentFactory = new DocumentFactory();
		editorEventObservable = new ImmediateNotifyingObservable();
		intervalEventObservable = new ImmediateNotifyingObservable();
		uiListener = new UIListener(editorEventObservable);
		addEditorObserversAndUIListeners();

		IntervalManager.getInstance().addIntervalListener(
				new IntervalLoggerObserver());
	}

	/** Creates change listeners for different document events. */
	private void addEditorObserversAndUIListeners() {
		editorEventObservable.addObserver(new EditorEventObserver(this));
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

	/** Closes the current interval (if it is not already closed). */
	/* package */void closeInterval(IntervalBase interval) {
		if (!interval.isClosed()) {
			Document document = null;
			if (interval instanceof UserActivityIntervalBase) {
				UserActivityIntervalBase activeInterval = (UserActivityIntervalBase) interval;
				document = documentFactory.createDocument(activeInterval
						.getPart());
			}
			interval.setDocument(document);
			interval.setEndTime(new Date());
			interval.setIsInDebugMode(WatchDogUtils.isInDebugMode());
			interval.closeInterval();
			recordedIntervals.add(interval);
			intervalEventObservable.notifyObservers(new ClosingIntervalEvent(
					interval));
		}
	}

	/** Creates a new editing interval. */
	/* package */void createNewInterval(IntervalBase interval, int timeout) {
		// TODO (MMB) shouldn't this handler be added to the interval itself?
		intervals.add(interval);
		addNewIntervalHandler(interval, timeout);
	}

	/**
	 * Adds a new interval handler base, and defines its timeout, i.e. when the
	 * interval is closed.
	 */
	private void addNewIntervalHandler(final IntervalBase interval,
			final int timeout) {
		interval.addTimeoutListener(timeout, new OnInactiveCallback() {
			@Override
			public void onInactive() {
				if (timeout == 0) {
					return;
				}
				closeInterval(interval);
			}
		});
		intervalEventObservable.notifyObservers(new NewIntervalEvent(interval));
	}

	/** Sets a list of recorded intervals. */
	/* package */void setRecordedIntervals(List<IntervalBase> intervals) {
		recordedIntervals = intervals;
	}

	/**
	 * @return the single ActivityInterval that is actually a
	 *         UserActivityInterval. There can only be one such interval at any
	 *         given time. If there is none, <code>null</code>.
	 */
	/* package */UserActivityIntervalBase getUserActivityIntervalIfAny() {
		for (IntervalBase interval : intervals) {
			if (interval instanceof UserActivityIntervalBase) {
				return (UserActivityIntervalBase) interval;
			}
		}
		return null;
	}

	/** Closes all currently open intervals. */
	public void closeAllCurrentIntervals() {
		for (IntervalBase interval : intervals) {
			closeInterval(interval);
		}
	}

	/** Returns a list of recorded intervals. */
	public List<IntervalBase> getRecordedIntervals() {
		return recordedIntervals;
	}

	/**
	 * @return editorEventObservable
	 */
	public ImmediateNotifyingObservable getEditorObserveable() {
		return editorEventObservable;
	}

	/** Registers a new interval listener. */
	public void addIntervalListener(Observer listener) {
		intervalEventObservable.addObserver(listener);
	}

	/** Removes an existing interval listener. */
	public void removeIntervalListener(Observer listener) {
		intervalEventObservable.deleteObserver(listener);
	}

	/** Starts and registers a new session interval. */
	public void startNewSessionInterval() {
		SessionInterval activeSessionInterval = new SessionInterval();
		addNewIntervalHandler(activeSessionInterval, 0);
	}
}
