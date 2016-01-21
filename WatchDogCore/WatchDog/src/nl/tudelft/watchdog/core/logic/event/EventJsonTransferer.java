package nl.tudelft.watchdog.core.logic.event;

import nl.tudelft.watchdog.core.logic.event.eventtypes.EventBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

/**
 * Transmits WatchDog events in a Json format to the WatchDog server. This is
 * basically a wrapper around {@link JsonTransferer} that initializes the
 * generic type and makes sure the correct URL is used to transfer the events
 * to.
 */
public class EventJsonTransferer extends JsonTransferer<EventBase> {
	
	@Override
	protected String getPostURL(String userId, String projectId) {
		return NetworkUtils.buildEventsPostURL(userId, projectId);
	}

}
