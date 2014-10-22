package nl.tudelft.watchdog.logic.interval;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nl.tudelft.watchdog.logic.document.DocumentCreator;
import nl.tudelft.watchdog.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.util.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

/** The interval manager handles the addition and removal */
public class IntervalManager extends IntervalManagerBase {

	private EditorIntervalBase editorInterval;

	/**
	 * The session seed, a random number generated on each instantiation of the
	 * IntervalManager to be able to tell running Eclipse instances apart.
	 */
	private long sessionSeed;

	private IntervalPersister intervalsToTransferPersister;

	private IntervalPersister intervalsStatisticsPersister;

	/** Constructor. */
	public IntervalManager(IntervalPersister intervalsToTransferPersister,
			IntervalPersister intervalsStatisticsPersister) {
		this.intervalsToTransferPersister = intervalsToTransferPersister;
		this.intervalsStatisticsPersister = intervalsStatisticsPersister;
		this.sessionSeed = WatchDogUtils.randomObject.nextLong();
	}

	/**
	 * Adds the supplied interval to the list of intervals. New intervals must
	 * use this method to be registered properly. Handles the addition of
	 * already closed intervals properly. Delegates to the correct adding
	 * mechanism.
	 */
	public void addInterval(IntervalBase interval) {
		interval.setSessionSeed(sessionSeed);
		if (interval instanceof EditorIntervalBase) {
			EditorIntervalBase editorInterval = (EditorIntervalBase) interval;
			addEditorInterval(editorInterval);
		} else {
			addRegularIntervalBase(interval);
		}

		WatchDogLogger.getInstance().logInfo(
				"Created interval " + interval + " " + interval.getType());
		if (interval.isClosed()) {
			closeInterval(interval);
		}
	}

	private void addRegularIntervalBase(IntervalBase interval) {
		if (intervals.size() > 30) {
			WatchDogLogger
					.getInstance()
					.logSevere(
							"Too many open intervals. Something fishy is going on here! Cannot add more intervals.");
			return;
		}
		intervals.add(interval);
	}

	/**
	 * Adds the given EditorIntervalBase, if the existing editorInterval is
	 * closed.
	 */
	private void addEditorInterval(EditorIntervalBase editorInterval) {
		if (!(this.editorInterval == null || this.editorInterval.isClosed())) {
			WatchDogLogger.getInstance().logSevere(
					"Failure: Unclosed editor interval! " + editorInterval);
			closeInterval(this.editorInterval, editorInterval.getStart());
		}

		this.editorInterval = editorInterval;
	}

	/**
	 * Closes the current interval (if it is not already closed). Handles
	 * <code>null</code> gracefully.
	 */
	public void closeInterval(IntervalBase interval, Date forcedDate) {
		if (interval == null) {
			return;
		}

		interval.setEndTime(forcedDate);
		closeInterval(interval);
	}

	private void closeInterval(IntervalBase interval) {
		if (interval == null) {
			return;
		}

		if (interval instanceof TypingInterval) {
			TypingInterval typingInterval = (TypingInterval) interval;
			typingInterval.setEndingDocument(DocumentCreator
					.createDocument(typingInterval.getEditor()));
		}

		interval.close();

		if (interval instanceof EditorIntervalBase) {
			editorInterval = null;
		} else {
			intervals.remove(interval);
		}
		intervalsToTransferPersister.saveInterval(interval);
		intervalsStatisticsPersister.saveInterval(interval);
		WatchDogLogger.getInstance().logInfo(
				"closed interval " + interval + " " + interval.getType());
	}

	/** Closes all currently open intervals. */
	public void closeAllIntervals() {
		Date closingDate = new Date();
		closeInterval(editorInterval, closingDate);
		Iterator<IntervalBase> iterator = intervals.listIterator();
		while (iterator.hasNext()) {
			// we need to remove the interval first from the list in order to
			// avoid ConcurrentListModification Exceptions.
			IntervalBase interval = iterator.next();
			iterator.remove();
			closeInterval(interval, closingDate);
		}
	}

	/**
	 * @return the single {@link EditorIntervalBase} that is actually a
	 *         {@link EditorIntervalBase}. There can only be one such interval
	 *         at any given time. If there is none, <code>null</code>.
	 */
	public EditorIntervalBase getEditorInterval() {
		return editorInterval;
	}

	/** Returns an immutable list of recorded intervals. */
	public List<IntervalBase> getOpenIntervals() {
		return Collections.unmodifiableList(intervals);
	}

	/**
	 * @return the statistics persister.
	 */
	public IntervalPersister getIntervalsStatisticsPersister() {
		return intervalsStatisticsPersister;
	}
}