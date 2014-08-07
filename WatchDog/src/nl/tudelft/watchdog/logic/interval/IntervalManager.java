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
import nl.tudelft.watchdog.logic.eclipseuireader.events.UserActionManager;
import nl.tudelft.watchdog.logic.eclipseuireader.events.listeners.UIListener;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.SessionInterval;
import nl.tudelft.watchdog.logic.interval.intervaltypes.UserActivityIntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

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
	private UIListener uiListener;

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

	private UserActionManager userActionManager;

	/** Private constructor. */
	private IntervalManager() {
		this.sessionSeed = new Random(new Date().getTime()).nextLong();

		File file = new File(
				Activator.getDefault().getStateLocation().toFile(),
				"intervals.mapdb");
		this.intervalPersister = new IntervalPersister(file);

		this.documentFactory = new DocumentFactory();
		userActionManager = new UserActionManager(this);
		this.uiListener = new UIListener(userActionManager,
				new IntervalTransferManager(intervalPersister));
		addNewSessionInterval();
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
	public/* package */void closeInterval(IntervalBase interval) {
		if (interval == null) {
			return;
		}
		interval.closeInterval();
		intervals.remove(interval);
		intervalPersister.saveInterval(interval);
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

	/** Starts and registers a new session interval. */
	public void addNewSessionInterval() {
		SessionInterval activeSessionInterval = new SessionInterval(
				getSessionSeed());
		intervals.add(activeSessionInterval);
	}

	/**
	 * @return The session seed, a random number generated on each start of
	 *         Eclipse to be able to tell running Eclipse instances apart.
	 */
	public long getSessionSeed() {
		return sessionSeed;
	}

	/**
	 * @return {@link UserActionManager}
	 */
	public UserActionManager getUserActionManager() {
		return userActionManager;
	}
}
