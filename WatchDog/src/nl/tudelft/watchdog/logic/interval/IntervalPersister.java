package nl.tudelft.watchdog.logic.interval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.NoSuchElementException;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;

import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 * Support for storing and querying intervals. The intervals are saved with a
 * long key, thus the maximum number of intervals that any single WatchDog
 * instance can record before the database breaks is {@link Long#MAX_VALUE}.
 */
public class IntervalPersister {

	/** In memory representation of the interval store */
	private NavigableMap<Long, IntervalBase> database;

	/**
	 * Create a new interval persister. If @path points to an existing database
	 * of intervals, it will be reused.
	 */
	public IntervalPersister(final String path) {
		DB db = DBMaker.newFileDB(new File(path)).closeOnJvmShutdown().make();
		database = db.getTreeMap("intervals");
	}

	/**
	 * Read all intervals starting from @from (inclusive) return them as a List.
	 */
	public List<IntervalBase> readIntevals(final long from) {
		return new ArrayList<IntervalBase>(database
				.subMap(from, Long.MAX_VALUE).values());
	}

	/**
	 * Read intervals between @from (inclusive) and @to (exclusive) and return
	 * them as a List.
	 */
	public List<IntervalBase> readIntevals(final long from, final long to) {
		return new ArrayList<IntervalBase>(database.subMap(from, to).values());
	}

	/** Save a list of intervals to persistent storage */
	public void saveIntervals(final List<IntervalBase> intervals) {
		for (IntervalBase interval : intervals) {
			saveInterval(interval);
		}
	}

	/** Saves one interval to persistent storage */
	public void saveInterval(IntervalBase interval) {
		long highestKey = 0;
		try {
			highestKey = getHighestKey();
		} catch (NoSuchElementException e) {
			// intentionally left empty, start with key 0 if database is empty.
		}
		database.put(highestKey + 1, interval);
	}

	/** @return The highest key in the database. */
	public long getHighestKey() {
		return database.lastKey();
	}
}
