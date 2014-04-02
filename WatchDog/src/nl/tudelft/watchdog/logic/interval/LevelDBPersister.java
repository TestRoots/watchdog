package nl.tudelft.watchdog.logic.interval;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.IntervalFactory;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

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
				batch.put(longToByteArray(interval.getStart().getTime()),
						interval.toJSON().getBytes());
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
		// options.createIfMissing(true);

		DB db = null;
		DBIterator iterator = null;
		try {
			db = factory.open(new File(dbFile), options);
			// ReadOptions ro = new ReadOptions();
			// ro.snapshot(db.getSnapshot());
			//
			// byte[] start = db.get(longToByteArray(from), ro);
			// byte[] end = db.get(longToByteArray(to), ro);

			List<IntervalBase> result = new ArrayList<IntervalBase>();
			iterator = db.iterator();
			for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
				long key = byteArraytoLong(iterator.peekNext().getKey());
				String value = asString(iterator.peekNext().getValue());
				System.out.println(key + " = " + value);
				IntervalBase val = IntervalFactory.fromJSON(value);
				result.add(val);
			}

		} catch (IOException exception) {
			exception.printStackTrace();
		} finally {
			try {
				iterator.close();
				db.close();
			} catch (IOException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}

		return new ArrayList<IntervalBase>();
	}

	public static byte[] longToByteArray(long l) {
		return ByteBuffer.allocate(8).putLong(l).array();
	}

	public static long byteArraytoLong(byte[] b) {
		return ByteBuffer.wrap(b).getLong();
	}
}
