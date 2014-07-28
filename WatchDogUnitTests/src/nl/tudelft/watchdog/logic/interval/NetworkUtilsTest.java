package nl.tudelft.watchdog.logic.interval;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.logic.NetworkUtils.Connection;
import nl.tudelft.watchdog.logic.ServerCommunicationException;
import nl.tudelft.watchdog.logic.document.Document;
import nl.tudelft.watchdog.logic.document.DocumentType;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.interval.active.SessionInterval;

import org.junit.Ignore;
import org.junit.Test;

/**
 * These tests rely on our public WatchDog service running. They are therefore
 * not mere unit, but more integration tests.
 */
public class NetworkUtilsTest {

	private String fooBarUser = "407c87ddd731a223ec30e6dc4a63971ed3b2e7b0";

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
		JsonTransferer it = new JsonTransferer();
		SessionInterval interval = new SessionInterval(0);
		ArrayList<IntervalBase> intervals = createSampleIntervals(interval);
		String json = it.toJson(intervals);

		try {
			NetworkUtils.transferJson(
					NetworkUtils.buildIntervalsPostURL(fooBarUser), json);
		} catch (ServerCommunicationException e) {
			fail(e.getMessage());
		}
	}

	private ArrayList<IntervalBase> createSampleIntervals(IntervalBase interval) {
		ArrayList<IntervalBase> intervals = new ArrayList<IntervalBase>();
		interval.setDocument(new Document("Project", "Production.java",
				DocumentType.PRODUCTION));
		interval.setStartTime(new Date(1));
		interval.setEndTime(new Date(2));
		interval.setIsInDebugMode(false);
		intervals.add(interval);
		return intervals;
	}

}
