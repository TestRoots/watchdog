package nl.tudelft.watchdog.core.logic.interval;

import java.io.File;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

/**
 * Support for storing and querying intervals. The intervals are saved with a
 * long key, thus the maximum number of intervals that any single WatchDog
 * instance can record before the database breaks is {@link Long#MAX_VALUE}.
 * 
 * This class is basically a wrapper around {@link PersisterBase} to avoid
 * having generic types all over the code base.
 */
public class IntervalPersisterBase extends PersisterBase<IntervalBase> {

	/** The name of the DB collection that stores the intervals. */
	private static final String INTERVALS = "intervals";

	/**
	 * Create a new interval persister. If file points to an existing database
	 * of intervals, it will be reused.
	 */
	public IntervalPersisterBase(final File file) {
		super(file, INTERVALS);
	}

}
