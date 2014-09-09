package nl.tudelft.watchdog.logic.interval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;

import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 * Support for storing and querying intervals. The intervals are saved with a
 * long key, thus the maximum number of intervals that any single WatchDog
 * instance can record before the database breaks is {@link Long#MAX_VALUE}.
 */
public class IntervalPersister {

	private static final String INTERVALS = "intervals";

	private DB database;

	/** In memory representation of the interval store */
	private NavigableMap<Long, IntervalBase> map;

	private File file;

	/**
	 * Create a new interval persister. If file points to an existing database
	 * of intervals, it will be reused.
	 */
	public IntervalPersister(final File file) {
		this.file = file;
		try {
			initalizeDatabase(file);
		} catch (Error e) {
			// MapDB wraps every exception inside an Error, so this code is
			// unfortunately necessary.
			try {
				recreateDatabase(file);
			} catch (Error innerError) {
				WatchDogLogger.getInstance().logSevere(innerError);
			}
		}
		try {
			// Compact database on every 10th new interval.
			if (!map.isEmpty() && map.size() % 10 == 0) {
				database.compact();
			}
		} catch (Error e) {
			// intentionally left empty.
		}
	}

	private void initalizeDatabase(File file) {
		database = createDatabase(file);
		try {
			map = database.getTreeMap(INTERVALS);
		} catch (RuntimeException e) {
			recreateDatabase(file);
		}
	}

	private DB createDatabase(final File file) {
		return DBMaker.newFileDB(file).closeOnJvmShutdown().make();
	}

	private void recreateDatabase(final File file) {
		closeDatabase();
		// Happens when an update to the serializables in the database
		// was made, and the new objects cannot be created from the old data
		deleteDatabaseFile();
		initalizeDatabase(file);
	}

	private void deleteDatabaseFile() {
		file.delete();
		File axuiliaryDatabaseFile = new File(file + ".p");
		axuiliaryDatabaseFile.delete();
		axuiliaryDatabaseFile = new File(file + ".t");
		axuiliaryDatabaseFile.delete();
	}

	/**
	 * Read all intervals starting from @from (inclusive) return them as a List.
	 */
	public List<IntervalBase> readIntervals(final long from) {
		return readIntervals(from, Long.MAX_VALUE);
	}

	/**
	 * Read intervals between @from (inclusive) and @to (exclusive) and return
	 * them as a List. In case of any database error, returns a new empty list.
	 */
	public List<IntervalBase> readIntervals(final long from, final long to) {
		try {
			return new ArrayList<IntervalBase>(map.subMap(from, to).values());
		} catch (RuntimeException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return new ArrayList<IntervalBase>();
	}

	/** Saves one interval to persistent storage */
	public void saveInterval(IntervalBase interval) {
		try {
			map.put(getHighestKey() + 1, interval);
			// persist changes to disk
			database.commit();
		} catch (Error error) {
			try {
				recreateDatabase(file);
			} catch (Error innerError) {
				WatchDogLogger.getInstance().logSevere(innerError);
			}
		}

	}

	/** @return The highest key in the database. -1, if the database is empty */
	public long getHighestKey() {
		try {
			return map.lastKey();
		} catch (RuntimeException e) {
			return -1;
		}
	}

	/**
	 * Properly close the database. Note: The database should be ACID even when
	 * not properly closed.
	 */
	public void closeDatabase() {
		if (database != null) {
			database.close();
		}
	}

	/** Clears the database on the computer and resets it. */
	public void clearAndResetMap() {
		if (database != null) {
			database.delete(INTERVALS);
			database.commit();
			map = database.getTreeMap(INTERVALS);
		}
	}
}
