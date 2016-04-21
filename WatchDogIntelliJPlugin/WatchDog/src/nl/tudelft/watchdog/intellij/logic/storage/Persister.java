package nl.tudelft.watchdog.intellij.logic.storage;

import nl.tudelft.watchdog.core.logic.storage.PersisterBase;
import nl.tudelft.watchdog.intellij.WatchDogStartUp;

import java.io.File;

/**
 * {@inheritDoc} IntelliJ's version of {@link PersisterBase} which handles ClassLoaders.
 */
public class Persister extends PersisterBase {

    private ClassLoader oldClassLoader = null;

    /**
     * {@inheritDoc}
     */
    public Persister(final File file) {
        super(file);
    }

    protected void replaceClassLoader() {
        oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WatchDogStartUp.class.getClassLoader());
    }

    protected void resetOldClassLoader() {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
}
