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
import nl.tudelft.watchdog.logic.interval.active.ActiveIntervalBase;
import nl.tudelft.watchdog.logic.interval.active.ActiveUserActivityIntervalBase;
import nl.tudelft.watchdog.logic.interval.activityCheckers.OnInactiveCallBack;
import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;
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
	/* package */void closeInterval(ActiveIntervalBase interval) {
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
	/* package */void createNewInterval(ActiveIntervalBase interval, int timeout) {
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

	/** Sets a list of recorded intervals. */
	/* package */void setRecordedIntervals(List<RecordedInterval> intervals) {
		recordedIntervals = intervals;
	}

	/**
	 * @return the single ActivityInterval that is actually a
	 *         UserActivityInterval. There can only be one such interval at any
	 *         given time. If there is none, <code>null</code>.
	 */
	/* package */ActiveIntervalBase getUserActivityIntervalIfAny() {
		for (ActiveIntervalBase interval : intervals) {
			if (interval instanceof ActiveUserActivityIntervalBase) {
				return interval;
			}
		}
		return null;
	}

	/** Closes all currently open intervals. */
	public void closeAllCurrentIntervals() {
		for (ActiveIntervalBase interval : intervals) {
			closeInterval(interval);
		}
	}

	/** Returns a list of recorded intervals. */
	public List<RecordedInterval> getRecordedIntervals() {
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
}
