package nl.tudelft.watchdog.core.logic.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import nl.tudelft.watchdog.core.logic.network.WatchDogTransferable;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * Support for storing and querying {@link WatchDogTransferable}s. The items are
 * saved with a long key, thus the maximum number of items that any single
 * WatchDog instance can record before the database breaks is
 * {@link Long#MAX_VALUE}.
 */
public abstract class PersisterBase<T extends WatchDogTransferable> {

	private boolean isClosed;

	/** The name of the DB collection to be used. */
	private final String collection;

	protected DB database;

	/** In memory representation of the store. */
	protected Set<T> set;

	protected File databaseFile;

	/**
	 * Create a new persister. If file points to an existing database, it will
	 * be reused.
	 */
	public PersisterBase(final File file, final String collectionName) {
		this.databaseFile = file;
		this.collection = collectionName;
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
			// Compact database on every 10th new item.
			if (!set.isEmpty() && set.size() % 10 == 0) {
				replaceClassLoader();
				database.compact();
				resetOldClassLoader();
			}
		} catch (Error e) {
			// intentionally left empty.
		}
	}

	protected void replaceClassLoader() {
	}

	protected void resetOldClassLoader() {
	}

	protected void initalizeDatabase(File file) {
		try {
			database = createDatabase(file);
			set = createSet();
		} catch (RuntimeException e) {
			recreateDatabase(file);
		}
	}

	private Set<T> createSet() {
		replaceClassLoader();
		Set<T> baseSet = database.getTreeSet(collection);
		resetOldClassLoader();
		return baseSet;
	}

	private DB createDatabase(final File file) {
		replaceClassLoader();
		DB database = DBMaker.newFileDB(file).closeOnJvmShutdown().make();
		isClosed = false;
		resetOldClassLoader();
		return database;
	}

	protected void recreateDatabase(final File file) {
		closeDatabase();
		// Happens when an update to the serializables in the database
		// was made, and the new objects cannot be created from the old data
		deleteDatabaseFile();
		initalizeDatabase(file);
	}

	protected void deleteDatabaseFile() {
		deleteOrOverwriteFileEmpty(null);
		deleteOrOverwriteFileEmpty(".p");
		deleteOrOverwriteFileEmpty(".t");
	}

	protected void deleteOrOverwriteFileEmpty(String extension) {
		File auxiliaryFile = databaseFile;
		if (extension != null) {
			auxiliaryFile = new File(databaseFile + extension);
		}

		if (!auxiliaryFile.delete()) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(databaseFile);
				fileOutputStream.write(new byte[] {});
			} catch (IOException exception) {
				WatchDogLogger.getInstance().logSevere(exception);
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException exception) {
					// intentionally empty
				} catch (NullPointerException exception) {
					// intentionally empty
				}
			}
		}
	}

	/**
	 * Reads all items in the collection and returns them as a List.
	 */
	public Set<T> readItems() {
		return set;
	}

	/**
	 * Saves one item to persistent storage
	 */
	public void saveItem(T item) {
		try {
			replaceClassLoader();
			set.add(item);
			// persist changes to disk
			database.commit();
			resetOldClassLoader();
		} catch (Error error) {
			try {
				recreateDatabase(databaseFile);
			} catch (Error innerError) {
				WatchDogLogger.getInstance().logSevere(innerError);
			}
		}
	}

	/**
	 * Removes the items from the database.
	 */
	public void removeItems(List<T> itemsToRemove) {
		replaceClassLoader();
		for (T item : itemsToRemove) {
			set.remove(item);
		}
		database.commit();
		resetOldClassLoader();
	}

	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * @return The highest key in the database. -1, if the database is empty
	 */
	public long getSize() {
		try {
			return set.size();
		} catch (Error e) {
			return -1;
		} catch (RuntimeException e) {
			return -1;
		}
	}

	/**
	 * Properly close the database. Note: The database should be ACID even when
	 * not properly closed.
	 */
	public void closeDatabase() {
		if (database != null && !database.isClosed()) {
			replaceClassLoader();
			database.close();
			resetOldClassLoader();
		}
		isClosed = true;
		resetOldClassLoader();
	}

	/**
	 * Clears the database on the computer and resets it.
	 */
	public void clearAndResetMap() {
		if (database != null && !database.isClosed()) {
			replaceClassLoader();
			database.delete(collection);
			database.commit();
			resetOldClassLoader();
			set = createSet();
		}
	}
}
