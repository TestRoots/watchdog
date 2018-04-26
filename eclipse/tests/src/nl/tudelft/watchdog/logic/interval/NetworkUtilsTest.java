package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IDEOpenInterval;
import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;
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
	public void user_does_not_exist_for_transfer() {
		String url = NetworkUtils.buildExistingUserURL("nonexistantSHA1");
		assertEquals(Connection.UNSUCCESSFUL,
				NetworkUtils.urlExistsAndReturnsStatus200(url));
	}

	@Test
	@Ignore
	public void user_exists_for_transfer() {
		String url = NetworkUtils.buildExistingUserURL(fooBarUser);
		assertEquals(Connection.SUCCESSFUL,
				NetworkUtils.urlExistsAndReturnsStatus200(url));
	}

	@Test
	@Ignore
	public void interval_transfer() {
		JsonTransferer it = new JsonTransferer();
		IntervalBase interval = new IDEOpenInterval(new Date());
		ArrayList<WatchDogItem> intervals = createSampleIntervals(interval);
		String json = it.toJson(intervals);

		try {
			NetworkUtils.transferJsonAndGetResponse(NetworkUtils
					.buildIntervalsPostURL(fooBarUser, fooBarProject), json);
		} catch (ServerCommunicationException | ServerReturnCodeException e) {
			fail(e.getMessage());
		}
	}

	private ArrayList<WatchDogItem> createSampleIntervals(IntervalBase interval) {
		ArrayList<WatchDogItem> intervals = new ArrayList<WatchDogItem>();
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		intervals.add(interval);
		return intervals;
	}

}
