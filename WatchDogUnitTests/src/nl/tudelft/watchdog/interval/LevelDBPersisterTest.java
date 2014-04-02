package nl.tudelft.watchdog.interval;

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
	public void before() {
		cleanup();
	}

	@After
	public void after() {
		cleanup();
	}

	@Test
	public void testWriteToDB() {
		LevelDBPersister persister = new LevelDBPersister(path);
		List<IntervalBase> intervals = genIntervalList(10);
		persister.saveIntervals(intervals);

		List<IntervalBase> loaded = persister.readIntevals(0, Long.MAX_VALUE);
		for (int i = 0; i < 10; i++) {
			assert (loaded.get(i).equals(intervals.get(i)));
		}
	}

	@Test
	public void readFromDB() {
		LevelDBPersister persister = new LevelDBPersister(path);
		persister.readIntevals(0, Long.MAX_VALUE);
	}

	@Test
	public void testlongToByteArray() {
		byte[] bytes = LevelDBPersister.longToByteArray(4);

		assert (bytes.length == 8);
		assert (bytes[7] == 4);

		bytes = LevelDBPersister.longToByteArray(16);

		assert (bytes.length == 8);
		assert (bytes[7] == 0xf);

		bytes = LevelDBPersister.longToByteArray(257);

		assert (bytes.length == 8);
		assert (bytes[7] == 0xff);
		assert (bytes[6] == 0x1);
	}

	@Test
	public void testByteArraytoLong() {
		byte[] bytes = { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x1, 0x12 };
		assert (LevelDBPersister.byteArraytoLong(bytes) == 268);
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
		i.setEndTime(new Date(i.getStart().getTime()
				+ (new Random()).nextInt(10000)));
		return i;
	}

	private void cleanup() {
		String s = Paths.get("").toAbsolutePath().toString();
		File toDel = new File(s, path);
		delete(toDel);
	}

	private void delete(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		f.delete();
	}
}
