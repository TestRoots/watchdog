package nl.tudelft.watchdog.intellij.logic.event;

import nl.tudelft.watchdog.core.logic.event.EventPersisterBase;
import nl.tudelft.watchdog.intellij.WatchDogStartUp;

import java.io.File;

/**
 * {@inheritDoc} IntelliJ's version of EventPersister which handles ClassLoaders.
 */
public class EventPersister extends EventPersisterBase {

    private ClassLoader oldClassLoader = null;

    /**
     * {@inheritDoc}
     */
    public EventPersister(File file) {
        super(file);
    }

    @Override
    protected void replaceClassLoader() {
        oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WatchDogStartUp.class.getClassLoader());
    }

    @Override
    protected void resetOldClassLoader() {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
}
