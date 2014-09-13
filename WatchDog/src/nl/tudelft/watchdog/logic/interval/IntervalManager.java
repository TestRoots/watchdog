package nl.tudelft.watchdog.logic.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentCreator;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalType;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

/** The interval manager handles the addition and removal */
public class IntervalManager {
	/** A list of currently opened intervals. */
	private List<IntervalBase> intervals = new ArrayList<IntervalBase>();

	private EditorIntervalBase editorInterval;

	/** The document factory. */
	private DocumentCreator documentFactory;

	/**
	 * The session seed, a random number generated on each instantiation of the
	 * IntervalManager to be able to tell running Eclipse instances apart.
	 */
	private long sessionSeed;

	private IntervalPersister persister;

	/** Constructor. */
	public IntervalManager(IntervalPersister persister,
			DocumentCreator documentFactory) {
		this.persister = persister;
		this.documentFactory = documentFactory;
		this.sessionSeed = WatchDogUtils.randomObject.nextLong();
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

	/**
	 * Adds the given EditorIntervalBase, if the existing editorInterval is
	 * closed.
	 */
	public void addEditorIntervalAndSetDocument(EditorIntervalBase editorInterval) {
		if (this.editorInterval == null || this.editorInterval.isClosed()) {
			this.editorInterval = editorInterval;
		}
		editorInterval.setSessionSeed(sessionSeed);
		editorInterval.setDocument(documentFactory
				.createDocument(editorInterval.getEditor()));
		WatchDogLogger.getInstance().logInfo(
				"created new editor interval " + editorInterval);
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
		if (interval instanceof EditorIntervalBase) {
			editorInterval = null;
		} else {
			intervals.remove(interval);
		}
		persister.saveInterval(interval);
		WatchDogLogger.getInstance().logInfo("closed interval " + interval);
	}

	/** Closes all currently open intervals. */
	public void closeAllIntervals() {
		closeInterval(editorInterval);
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
		return editorInterval;
	}

	/**
	 * @return Returns an interval of the given class, if there is any such
	 *         open. If not, returns null.
	 */
	@SuppressWarnings("unchecked")
	public <T extends IntervalBase> T getIntervalOfClass(Class<T> clazz) {
		for (IntervalBase interval : intervals) {
			if (clazz.isAssignableFrom(interval.getClass())
					&& !interval.isClosed()) {
				return (T) interval;
			}
		}
		return null;
	}

	/** Returns an immutable list of recorded intervals. */
	public List<IntervalBase> getOpenIntervals() {
		return Collections.unmodifiableList(intervals);
	}
}