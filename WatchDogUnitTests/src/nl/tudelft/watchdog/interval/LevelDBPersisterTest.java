package nl.tudelft.watchdog.interval;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.LevelDBPersister;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;

import org.junit.Before;
import org.junit.Test;

public class LevelDBPersisterTest {

	public static final String path = "test.leveldb";

	@Before
	public void before() {
		String s = Paths.get("").toAbsolutePath().toString() + 
				File.pathSeparator + path;
		File toDel = new File(s);
		delete(toDel);
	}

	@Test
	public void testWriteToDB() {
		LevelDBPersister persister = new LevelDBPersister(path);

		List<IntervalBase> intervals = new ArrayList<IntervalBase>();
		for (int i = 0; i < 10; i++) {
			intervals.add(rndInterval());
		}

		persister.saveIntervals(intervals);
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

	private IntervalBase rndInterval() {
		return new SessionInterval();
	}

	void delete(File f) {
		System.err.println("Deleting " + f.getAbsolutePath());
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		f.delete();
	}
}
