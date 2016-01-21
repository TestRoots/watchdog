package nl.tudelft.watchdog.core.logic.interval;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

public class IntervalJsonTransferer extends JsonTransferer<IntervalBase> {

	@Override
	protected String getPostURL(String userId, String projectId) {
		return NetworkUtils.buildIntervalsPostURL(userId, projectId);
	}

}
