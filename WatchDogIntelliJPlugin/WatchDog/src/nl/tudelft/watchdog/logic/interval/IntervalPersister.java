package nl.tudelft.watchdog.logic.interval;


import nl.tudelft.watchdog.WatchDog;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Support for storing and querying intervals. The intervals are saved with a
 * long key, thus the maximum number of intervals that any single WatchDog
 * instance can record before the database breaks is {@link Long#MAX_VALUE}.
 */
public class IntervalPersister {

    private static final String INTERVALS = "intervals";

    private DB database;

    private boolean isClosed;

    /**
     * In memory representation of the interval store
     */
    private Set<IntervalBase> set;

    private File databaseFile;


    private ClassLoader oldClassLoader = null;

    /**
     * Create a new interval persister. If file points to an existing database
     * of intervals, it will be reused.
     */
    public IntervalPersister(final File file) {
        this.databaseFile = file;
        try {
            initalizeDatabase(file);
        } catch (Error e) {
            // MapDB wraps every exception inside an Error, so this code is
            // unfortunately necessary.
            try {
                recreateDatabase(file);
            } catch (Error innerError) {
                WatchDogLogger.getInstance(Preferences.getInstance().isLoggingEnabled()).logSevere(innerError);
            }
        }
        try {
            // Compact database on every 10th new interval.
            if (!set.isEmpty() && set.size() % 10 == 0) {
                replaceClassLoader();
                database.compact();
                resetOldClassLoader();
            }
        } catch (Error e) {
            // intentionally left empty.
        }
    }

    private void replaceClassLoader() {
        oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WatchDog.class.getClassLoader());
    }

    private void resetOldClassLoader() {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }

    private void initalizeDatabase(File file) {
        try {
            database = createDatabase(file);
            set = createSet();
        } catch (RuntimeException e) {
            recreateDatabase(file);
        }
    }

    private Set<IntervalBase> createSet() {
        replaceClassLoader();
        Set<IntervalBase> intervalBaseSet = database.getTreeSet(INTERVALS);
        resetOldClassLoader();
        return intervalBaseSet;
    }

    private DB createDatabase(final File file) {
        replaceClassLoader();
        DB database = DBMaker.newFileDB(file).closeOnJvmShutdown().make();
        isClosed = false;
        resetOldClassLoader();
        return database;
    }

    private void recreateDatabase(final File file) {
        closeDatabase();
        // Happens when an update to the serializables in the database
        // was made, and the new objects cannot be created from the old data
        deleteDatabaseFile();
        initalizeDatabase(file);
    }

    private void deleteDatabaseFile() {
        deleteOrOverwriteFileEmpty(null);
        deleteOrOverwriteFileEmpty(".p");
        deleteOrOverwriteFileEmpty(".t");
    }

    private void deleteOrOverwriteFileEmpty(String extension) {
        File auxiliaryFile = databaseFile;
        if (extension != null) {
            auxiliaryFile = new File(databaseFile + extension);
        }

        if (!auxiliaryFile.delete()) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(
                        databaseFile);
                fileOutputStream.write(new byte[]{});
                fileOutputStream.close();
            } catch (IOException exception) {
                WatchDogLogger.getInstance(Preferences.getInstance().isLoggingEnabled()).logSevere(exception);
            }
        }
    }

    /**
     * Reads all intervals and returns them as a List.
     */
    public Set<IntervalBase> readIntervals() {
        return set;
    }

    /**
     * Saves one interval to persistent storage
     */
    public void saveInterval(IntervalBase interval) {
        try {
            replaceClassLoader();
            set.add(interval);
            // persist changes to disk
            database.commit();
            resetOldClassLoader();
        } catch (Error error) {
            try {
                recreateDatabase(databaseFile);
            } catch (Error innerError) {
                WatchDogLogger.getInstance(Preferences.getInstance().isLoggingEnabled()).logSevere(innerError);
            }
        }
    }

    /**
     * Removes the intervals from the database..
     */
    public void removeIntervals(List<IntervalBase> intervalsToRemove) {
        replaceClassLoader();
        for (IntervalBase interval : intervalsToRemove) {
            set.remove(interval);
        }
        database.commit();
        resetOldClassLoader();
    }

    /**
     * @return The highest key in the database. -1, if the database is empty
     */
    public long getSize() {
        try {
            return set.size();
        } catch (RuntimeException e) {
            return -1;
        } catch (Error e) {
            return -1;
        }
    }

    public boolean isClosed() {
        return isClosed;
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
            database.delete(INTERVALS);
            database.commit();
            resetOldClassLoader();
            set = createSet();
        }
    }
}
