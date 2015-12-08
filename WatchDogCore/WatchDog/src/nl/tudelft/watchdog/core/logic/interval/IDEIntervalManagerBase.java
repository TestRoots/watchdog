package nl.tudelft.watchdog.core.logic.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.EditorIntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.TypingInterval;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * Adds functionality for adding and removing intervals.
 */
public abstract class IDEIntervalManagerBase extends IntervalManagerBase {
	
	private EditorIntervalBase editorInterval;

	/**
	 * The session seed, a random number generated on each instantiation of the
	 * IntervalManager to be able to tell running Eclipse instances apart.
	 */
	private String sessionSeed;

	private IntervalPersisterBase intervalsToTransferPersister;

	private IntervalPersisterBase intervalsStatisticsPersister;

	/** Constructor. */
	protected IDEIntervalManagerBase(IntervalPersisterBase intervalsToTransferPersister,
			IntervalPersisterBase intervalsStatisticsPersister) {
		this.intervalsToTransferPersister = intervalsToTransferPersister;
		this.intervalsStatisticsPersister = intervalsStatisticsPersister;
		generateAndSetSessionSeed();
	}

	/** Generates and sets a new random session seed. */
	public void generateAndSetSessionSeed() {
		this.sessionSeed = RandomStringUtils.randomAlphabetic(40);
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
	 * Updates the given EditorIntervalBase.
	 */
	private void addEditorInterval(EditorIntervalBase editorInterval) {
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
			setEndingDocumentOf(typingInterval);
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

	/**
	 * Sets the ending document of the given typing interval.
	 * To be implemented by the subclasses for specific IDEs.
	 */
	protected abstract void setEndingDocumentOf(TypingInterval typingInterval);

	/** Closes all currently open intervals. */
	public void closeAllIntervals() {
		Date closingDate = new Date();
		closeAllIntervals(closingDate);
	}

	/** Closes all currently open intervals with the supplied closing date. */
	public void closeAllIntervals(Date closingDate) {
		closeInterval(editorInterval, closingDate);
		ArrayList<IntervalBase> copiedIntervals = new ArrayList<IntervalBase>(intervals);
		Iterator<IntervalBase> iterator = copiedIntervals.listIterator();
		while (iterator.hasNext()) {
			// we need to remove the interval first from the list in order to
			// avoid ConcurrentListModification Exceptions.
			IntervalBase interval = iterator.next();
			iterator.remove();
			closeInterval(interval, closingDate);
		}
		intervals.clear();
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
	public IntervalPersisterBase getIntervalsStatisticsPersister() {
		return intervalsStatisticsPersister;
	}

}
