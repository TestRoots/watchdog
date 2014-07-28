package nl.tudelft.watchdog.logic.interval;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;

import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 * Support for storing and querying intervals.
 */
public class IntervalPersister {

	/** In memory representation of the interval store */
	private NavigableMap<Long, IntervalBase> intervals;

	/**
	 * Create a new interval persister. If @path points to an existing database
	 * of intervals, this will be reused.
	 */
	public IntervalPersister(final String path) {
		DB db = DBMaker.newFileDB(new File(path)).closeOnJvmShutdown().make();
		intervals = db.getTreeMap("intervals");
	}

	/**
	 * Read intervals between @from (inclusive) and @to (exclusive) and return
	 * them as a List.
	 */
	public List<IntervalBase> readIntevals(final Date from, final Date to) {

		return new ArrayList<IntervalBase>(intervals.subMap(from.getTime(),
				to.getTime()).values());
	}

	/**
	 * Save a list of intervals to persistent storage
	 */
	public void saveIntervals(final List<IntervalBase> ivals) {
		for (IntervalBase i : ivals) {
			intervals.put(i.getStart().getTime(), i);
		}
	}
}
