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

	private DB database;

	/** In memory representation of the interval store */
	private NavigableMap<Long, IntervalBase> map;

	/**
	 * Create a new interval persister. If @path points to an existing database
	 * of intervals, it will be reused.
	 */
	public IntervalPersister(final File file) {
		database = DBMaker.newFileDB(file).closeOnJvmShutdown().make();
		map = database.getTreeMap("intervals");
		// Compact database on every 100th new interval.
		if (!map.isEmpty() && map.size() % 100 == 0) {
			database.compact();
		}
	}

	/**
	 * Read all intervals starting from @from (inclusive) return them as a List.
	 */
	public List<IntervalBase> readIntevals(final long from) {
		return new ArrayList<IntervalBase>(map.subMap(from, Long.MAX_VALUE)
				.values());
	}

	/**
	 * Read intervals between @from (inclusive) and @to (exclusive) and return
	 * them as a List.
	 */
	public List<IntervalBase> readIntevals(final long from, final long to) {
		return new ArrayList<IntervalBase>(map.subMap(from, to).values());
	}

	/**
	 * Save a list of intervals to the persistent storage, as a single commit
	 * (and hence in a very performance-oriented way)
	 */
	public void saveIntervals(final List<IntervalBase> intervals) {
		long newKey = getHighestKey() + 1;
		for (IntervalBase interval : intervals) {
			map.put(newKey, interval);
			++newKey;
		}
		database.commit();
	}

	/** Saves one interval to persistent storage */
	public void saveInterval(IntervalBase interval) {
		map.put(getHighestKey() + 1, interval);
		database.commit(); // persist changes to disk
	}

	/** @return The highest key in the database. */
	public long getHighestKey() {
		try {
			return map.lastKey();
		} catch (NoSuchElementException e) {
			return -1;
		}
	}

	/**
	 * Properly close the database. Note: The database should be ACID even when
	 * not properly closed.
	 */
	public void closeDatabase() {
		database.close();
	}
}
