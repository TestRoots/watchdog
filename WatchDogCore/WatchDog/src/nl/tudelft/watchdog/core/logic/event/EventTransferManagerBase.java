package nl.tudelft.watchdog.core.logic.event;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.TransferManagerBase;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;

/**
 * This manager takes care of the repeated transferal of all events to the
 * server. When the transfer to the server was successful, the events are
 * immediately deleted from the local database. Furthermore, it allows the
 * immediate execution of this regularly scheduled task, e.g. when it is needed
 * on exiting.
 */
public class EventTransferManagerBase extends TransferManagerBase<EventBase> {

	private static final int UPDATE_RATE = 3 * 60 * 1000;

	/**
	 * Constructor. Tries to immediately transfer all remaining events, and sets
	 * up a scheduled timer to run every {@value #UPDATE_RATE} milliseconds.
	 */
	public EventTransferManagerBase(final EventPersisterBase eventPersisterBase, String projectName) {
		super(eventPersisterBase, projectName, UPDATE_RATE);
	}

	@Override
	protected JsonTransferer<EventBase> createTransferer() {
		return new EventJsonTransferer();
	}

	@Override
	protected void updateStatisticsPreferences(int transferredEvents) {
		PreferencesBase prefs = WatchDogGlobals.getPreferences();
		prefs.setLastTransferedEvent();
		prefs.addTransferedEvents(transferredEvents);
	}

}
