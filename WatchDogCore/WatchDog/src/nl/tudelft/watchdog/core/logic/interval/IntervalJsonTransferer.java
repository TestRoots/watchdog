package nl.tudelft.watchdog.core.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

/**
 * Transmits WatchDog intervals in a Json format to the WatchDog server. This is
 * basically a wrapper around {@link JsonTransferer} that initializes the
 * generic type and makes sure the correct URL is used to transfer the intervals
 * to.
 */
public class IntervalJsonTransferer extends JsonTransferer<IntervalBase> {

	@Override
	protected String getPostURL(String userId, String projectId) {
		return NetworkUtils.buildIntervalsPostURL(userId, projectId);
	}

}
