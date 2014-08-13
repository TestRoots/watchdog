package nl.tudelft.watchdog.logic.interval;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.Activator;
import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActivityIntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.logic.ui.EventManager;
import nl.tudelft.watchdog.logic.ui.listeners.WorkbenchListener;

/**
 * Manages interval listeners and keeps track of all intervals. Implements the
 * observer pattern, i.e. listeners can subscribe to interval events and will be
 * notified by an implementation of the {@link RecordedIntervalManager}. Is a
 * singleton.
 */
public class IntervalManager {

	/** A list of currently opened intervals. */
	private List<IntervalBase> intervals = new ArrayList<IntervalBase>();

	/** The UI listener */
	private WorkbenchListener uiListener;

	/** The document factory. */
	private DocumentFactory documentFactory;

	/** The interval persistence storage. */
	private IntervalPersister intervalPersister;

	/** The singleton instance of the interval manager. */
	private static IntervalManager instance = null;

	/**
	 * The session seed, a random number generated on each instantiation of the
	 * IntervalManager to be able to tell running Eclipse instances apart.
	 */
	private long sessionSeed;

	private EventManager userActionManager;

	/** Private constructor. */
	private IntervalManager() {
		this.sessionSeed = new Random(new Date().getTime()).nextLong();

		File file = new File(
				Activator.getDefault().getStateLocation().toFile(),
				"intervals.mapdb");
		this.intervalPersister = new IntervalPersister(file);

		this.documentFactory = new DocumentFactory();
		userActionManager = new EventManager(this);
		this.uiListener = new WorkbenchListener(userActionManager,
				new IntervalTransferManager(intervalPersister));
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
	public void addAndSetNewActiveInterval(UserActivityIntervalBase interval,
			int timeout) {
		intervals.add(interval);
		interval.setDocument(documentFactory.createDocument(interval.getPart()));
		WatchDogLogger.getInstance()
				.logInfo("created new interval " + interval);
	}

	/**
	 * Closes the current interval (if it is not already closed). Handles
	 * <code>null</code> gracefully.
	 */
	public void closeInterval(IntervalBase interval) {
		if (interval == null) {
			return;
		}
		interval.closeInterval();
		intervals.remove(interval);
		intervalPersister.saveInterval(interval);
	}

	/** Closes all currently open intervals. */
	public void closeAllIntervals() {
		Iterator<IntervalBase> iterator = intervals.listIterator();
		while (iterator.hasNext()) {
			// we need to remove the interval first from the list in order to
			// avoid ConcurrentListModification Exceptions.
			IntervalBase interval = iterator.next();
			iterator.remove();
			closeInterval(interval);
		}
	}

	/**
	 * @return An interval of the specified type, or <code>null</code> if no
	 *         such interval is currently open.
	 */
	public IntervalBase getIntervalOfType(IntervalType type) {
		for (IntervalBase interval : intervals) {
			if (interval.getActivityType() == type && !interval.isClosed()) {
				return interval;
			}
		}
		return null;
	}

	/**
	 * @return the single ActivityInterval that is actually a
	 *         UserActivityInterval. There can only be one such interval at any
	 *         given time. If there is none, <code>null</code>.
	 */
	public UserActivityIntervalBase getUserActivityIntervalIfAny() {
		for (IntervalBase interval : intervals) {
			if (interval instanceof UserActivityIntervalBase) {
				return (UserActivityIntervalBase) interval;
			}
		}
		return null;
	}

	/** Returns an immutable list of recorded intervals. */
	public List<IntervalBase> getOpenIntervals() {
		return Collections.unmodifiableList(intervals);
	}

	/** @return {@link EventManager} */
	public EventManager getUserActionManager() {
		return userActionManager;
	}

	/**
	 * Adds the supplied interval to the list of intervals. New intervals must
	 * use this method to be registered properly. Hanldes the addition of
	 * already closed intervals properly.
	 */
	public void addInterval(IntervalBase interval) {
		if (intervals.size() > 20) {
			WatchDogLogger
					.getInstance()
					.logSevere(
							"Too many open intervals. Something fishy is going on here! Cannot add more intervals.");
			return;
		}
		interval.setSessionSeed(sessionSeed);
		intervals.add(interval);
		if (interval.isClosed()) {
			closeInterval(interval);
		}
	}
}
