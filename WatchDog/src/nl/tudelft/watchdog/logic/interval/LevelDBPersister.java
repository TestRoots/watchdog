package nl.tudelft.watchdog.logic.interval;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.IntervalFactory;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.WriteBatch;

import com.google.common.primitives.Longs;

public class LevelDBPersister {

	private String dbFile;

	public LevelDBPersister(String file) {
		dbFile = file;
	}

	public void saveIntervals(List<IntervalBase> intervals) {
		Options options = new Options();
		options.createIfMissing(true);

		DB db = null;
		WriteBatch batch = null;
		try {
			db = factory.open(new File(dbFile), options);
			batch = db.createWriteBatch();

			for (IntervalBase interval : intervals) {
				byte[] bytes = longToByteArray(interval.getStart().getTime());
				batch.put(bytes, interval.toJSON().getBytes());
			}
			db.write(batch);
		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			try {
				if (batch != null) {
					batch.close();
				}

				if (db != null) {
					db.close();
				}

			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	public List<IntervalBase> readIntevals(long from, long to) {
		Options options = new Options();
		ReadOptions readOptions = new ReadOptions();

		DB db = null;
		DBIterator iterator = null;
		try {
			db = factory.open(new File(dbFile), options);
			readOptions.snapshot(db.getSnapshot());

			byte[] start = db.get(longToByteArray(from), readOptions);
			byte[] end = db.get(longToByteArray(to), readOptions);

			List<IntervalBase> result = new ArrayList<IntervalBase>();
			iterator = db.iterator(readOptions);
			iterator.seek(start);

			for (iterator.seekToFirst(); !Arrays.equals(iterator.peekNext()
					.getKey(), end)
					&& iterator.hasNext(); iterator.next()) {
				String value = asString(iterator.peekNext().getValue());
				result.add(IntervalFactory.fromJSON(value));
			}
			return result;

		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			try {
				iterator.close();
				readOptions.snapshot().close();
				db.close();
			} catch (IOException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}

		return new ArrayList<IntervalBase>();
	}

	public static byte[] longToByteArray(long l) {
		return Longs.toByteArray(l);
	}

	public static long byteArraytoLong(byte[] b) {
		return Longs.fromByteArray(b);
	}
}
