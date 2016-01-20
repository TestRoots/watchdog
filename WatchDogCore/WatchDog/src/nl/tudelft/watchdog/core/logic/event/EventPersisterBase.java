package nl.tudelft.watchdog.core.logic.event;

import java.io.File;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.storage.PersisterBase;

/**
 * Support for storing and querying events. The events are saved with a long
 * key, thus the maximum number of events that any single WatchDog instance can
 * record before the database breaks is {@link Long#MAX_VALUE}.
 * 
 * This class is basically a wrapper around {@link PersisterBase} to avoid
 * having generic types all over the code base.
 */
public class EventPersisterBase extends PersisterBase<EventBase> {

	/** The name of the DB collection that stores the events. */
	private static final String EVENTS = "events";

	/**
	 * Create a new event persister. If file points to an existing database of
	 * events, it will be reused.
	 */
	public EventPersisterBase(final File file) {
		super(file, EVENTS);
	}

}
