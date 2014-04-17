package nl.tudelft.watchdog.interval;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nl.tudelft.watchdog.logic.interval.LevelDBPersister;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LevelDBPersisterTest {

	public static final String path = "test.leveldb";

	@Before
	@After
	public void cleanup() {
		String s = Paths.get("").toAbsolutePath().toString();
		File toDel = new File(s, path);
		delete(toDel);
	}

	@Test
	public void testInteractionWithLevelDB() {
		LevelDBPersister persister = new LevelDBPersister(path);
		List<IntervalBase> intervals = genIntervalList(10);

		/*
		 * On absence of a better way of peeking at what was written, we just
		 * count the loaded items.
		 */
		persister.saveIntervals(intervals);
		List<IntervalBase> loadedIntervals = persister.readIntevals(0,
				Long.MAX_VALUE);
		assertEquals(intervals.size(), loadedIntervals.size());

		// Read them back and ensure they are sorted by key
		List<IntervalBase> loaded = persister.readIntevals(0, Long.MAX_VALUE);
		for (int i = 0; i < 9; i++) {
			assert (loaded.get(i).getStart().getTime() < 
					loaded.get(i + 1).getStart().getTime());
		}
	}

	@Test
	public void testlongToByteArray() {
		byte[] bytes = LevelDBPersister.longToByteArray(4);

		assertEquals(bytes.length, 8);
		assertEquals(bytes[7], 4);

		bytes = LevelDBPersister.longToByteArray(16);

		assertEquals(bytes.length, 8);
		assertEquals(bytes[7], 0x10);

		bytes = LevelDBPersister.longToByteArray(257);

		assertEquals(bytes.length, 8);
		assertEquals(bytes[7], 0x1);
		assertEquals(bytes[6], 0x1);
	}

	@Test
	public void testByteArraytoLong() {
		byte[] bytes = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1, 0x12 };
		assertEquals(LevelDBPersister.byteArraytoLong(bytes), 274);
	}

	private List<IntervalBase> genIntervalList(int n) {

		List<IntervalBase> intervals = new ArrayList<IntervalBase>();
		for (int i = 0; i < n; i++) {
			intervals.add(rndInterval());
		}
		return intervals;
	}

	private IntervalBase rndInterval() {
		SessionInterval i = new SessionInterval();
		i.setStartTime(new Date(i.getStart().getTime()
				+ (new Random()).nextInt(1000)));
		i.setEndTime(new Date(i.getStart().getTime()
				+ (new Random()).nextInt(100000)));
		return i;
	}

	private void delete(File file) {
		if (file.isDirectory()) {
			for (File other : file.listFiles())
				delete(other);
		}
		file.delete();
	}
}
