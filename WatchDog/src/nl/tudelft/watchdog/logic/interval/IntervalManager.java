package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import java.util.Random;

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

	/** The recorded intervals of this session */
	private List<IntervalBase> recordedIntervals = new ArrayList<IntervalBase>();

	/** Notifies subscribers of editorEvents. */
	private ImmediateNotifyingObservable editorEventObservable;

	/** Notifies subscribers of intervalEvents. */
	private ImmediateNotifyingObservable intervalEventObservable;

	/** The UI listener */
	private UIListener uiListener;

	/** The document factory. */
	private DocumentFactory documentFactory;

	/** The singleton instance of the interval manager. */
	private static IntervalManager instance = null;

	/**
	 * The session seed, a random number generated on each instantiation of the
	 * IntervalManager to be able to tell running Eclipse instances apart.
	 */
	private long sessionSeed;

	/** Private constructor. */
	private IntervalManager() {
		// setup logging
		this.intervalEventObservable = new ImmediateNotifyingObservable();
		addIntervalListener(new IntervalLoggerObserver());

		this.sessionSeed = new Random(new Date().getTime()).nextLong();
		addNewSessionInterval();
		this.documentFactory = new DocumentFactory();
		this.editorEventObservable = new ImmediateNotifyingObservable();
		this.uiListener = new UIListener(editorEventObservable);
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

	/**
	 * Closes the current interval (if it is not already closed). Handles
	 * <code>null</code> gracefully.
	 */
	/* package */void closeInterval(IntervalBase interval) {
		if (interval != null && !interval.isClosed()) {
			intervalEventObservable.notifyObservers(new ClosingIntervalEvent(
					interval));

			if (interval instanceof UserActivityIntervalBase) {
				UserActivityIntervalBase activeInterval = (UserActivityIntervalBase) interval;
				interval.setDocument(documentFactory
						.createDocument(activeInterval.getPart()));
			}
			interval.setEndTime(new Date());
			interval.setIsInDebugMode(WatchDogUtils.isInDebugMode());
			interval.closeInterval();

			intervals.remove(interval);
			recordedIntervals.add(interval);
		}
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
		Iterator<IntervalBase> iterator = intervals.listIterator();
		while (iterator.hasNext()) {
			// we need to remove the interval first from the list in order to
			// avoid ConcurrentListModification Exceptions.
			IntervalBase interval = iterator.next();
			iterator.remove();
			closeInterval(interval);
		}
	}

	/** Returns an immutable list of recorded intervals. */
	public List<IntervalBase> getClosedIntervals() {
		return Collections.unmodifiableList(recordedIntervals);
	}

	/** Returns an immutable list of recorded intervals. */
	public List<IntervalBase> getOpenIntervals() {
		return Collections.unmodifiableList(intervals);
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
	public void addNewSessionInterval() {
		SessionInterval activeSessionInterval = new SessionInterval(
				getSessionSeed());
		intervals.add(activeSessionInterval);
		addNewIntervalHandler(activeSessionInterval, 0);
	}

	/**
	 * @return The session seed, a random number generated on each start of
	 *         Eclipse to be able to tell running Eclipse instances apart.
	 */
	public long getSessionSeed() {
		return sessionSeed;
	}
}
