package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.interval.IntervalJsonTransferer;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IDEOpenInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.logic.network.ServerReturnCodeException;

/**
 * These tests rely on our public WatchDog service running. They are therefore
 * not mere unit, but more integration tests.
 */
public class NetworkUtilsTest {

	private String fooBarUser = "407c87ddd731a223ec30e6dc4a63971ed3b2e7b0";

	private String fooBarProject = "";

	@Test
	@Ignore
	public void testUserDoesNotExistTransfer() {
		String url = NetworkUtils.buildExistingUserURL("nonexistantSHA1");
		assertEquals(Connection.UNSUCCESSFUL,
				NetworkUtils.urlExistsAndReturnsStatus200(url));
	}

	@Test
	@Ignore
	public void testUserExistsTransfer() {
		String url = NetworkUtils.buildExistingUserURL(fooBarUser);
		assertEquals(Connection.SUCCESSFUL,
				NetworkUtils.urlExistsAndReturnsStatus200(url));
	}

	@Test
	@Ignore
	public void testIntervalTransfer() {
		IntervalJsonTransferer it = new IntervalJsonTransferer();
		IntervalBase interval = new IDEOpenInterval(new Date());
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);
		String json = it.toJson(intervals);

		try {
			NetworkUtils.transferJsonAndGetResponse(NetworkUtils
					.buildIntervalsPostURL(fooBarUser, fooBarProject), json);
		} catch (ServerCommunicationException | ServerReturnCodeException e) {
			fail(e.getMessage());
		}
	}

	private ArrayList<IntervalBase> createSampleIntervals(IntervalBase interval) {
		ArrayList<IntervalBase> intervals = new ArrayList<IntervalBase>();
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		intervals.add(interval);
		return intervals;
	}

}
