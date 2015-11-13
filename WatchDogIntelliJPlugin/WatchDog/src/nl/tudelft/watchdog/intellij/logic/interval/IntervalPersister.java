package nl.tudelft.watchdog.intellij.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.IntervalPersisterBase;
import nl.tudelft.watchdog.intellij.WatchDog;

import java.io.File;

/**
 * {@inheritDoc} IntelliJ's version of IntervalPersister which handles ClassLoaders.
 */
public class IntervalPersister extends IntervalPersisterBase {

    private ClassLoader oldClassLoader = null;

    /**
     * {@inheritDoc}
     */
    public IntervalPersister(final File file) {
        super(file);
    }

    protected void replaceClassLoader() {
        oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WatchDog.class.getClassLoader());
    }

    protected void resetOldClassLoader() {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
}
