package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.logic.document.DocumentFactory;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

/** The interval manager handles the addition and removal */
public class IntervalManager {
	/** A list of currently opened intervals. */
	private List<IntervalBase> intervals = new ArrayList<IntervalBase>();

	/** The document factory. */
	private DocumentFactory documentFactory;

	/**
	 * The session seed, a random number generated on each instantiation of the
	 * IntervalManager to be able to tell running Eclipse instances apart.
	 */
	private long sessionSeed;

	private IntervalPersister persister;

	/** Constructor. */
	public IntervalManager(IntervalPersister persister,
			DocumentFactory documentFactory) {
		this.persister = persister;
		this.documentFactory = documentFactory;
		this.sessionSeed = new Random(new Date().getTime()).nextLong();
	}

	/**
	 * Adds the supplied interval to the list of intervals. New intervals must
	 * use this method to be registered properly. Handles the addition of
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

	/** Creates a new editing interval. */
	public void addAndSetEditorInterval(EditorIntervalBase interval) {
		addInterval(interval);
		interval.setDocument(documentFactory.createDocument(interval
				.getEditor()));
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
		interval.close();
		intervals.remove(interval);
		persister.saveInterval(interval);
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
			if (interval.getType() == type && !interval.isClosed()) {
				return interval;
			}
		}
		return null;
	}

	/**
	 * @return the single {@link EditorIntervalBase} that is actually a
	 *         {@link EditorIntervalBase}. There can only be one such interval
	 *         at any given time. If there is none, <code>null</code>.
	 */
	public EditorIntervalBase getEditorInterval() {
		for (IntervalBase interval : intervals) {
			if (interval instanceof EditorIntervalBase) {
				return (EditorIntervalBase) interval;
			}
		}
		return null;
	}

	/** Returns an immutable list of recorded intervals. */
	public List<IntervalBase> getOpenIntervals() {
		return Collections.unmodifiableList(intervals);
	}
}