package nl.tudelft.watchdog.logic.interval;

import static nl.tudelft.watchdog.util.GSONUtil.gson;

import java.io.IOException;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Transmits the currently recorded intervals to the WatchDog server.
 */
public class IntervalTransferer {

	/** Sends the recorded intervals to the server. */
	public void sendIntervals() {
		List<IntervalBase> recordedIntervals = IntervalManager.getInstance()
				.getRecordedIntervals();
		String userid = WatchdogPreferences.getInstance().getUserid();
		String json = prepareIntervals(recordedIntervals);
		transferData(userid, json);
	}

	/** Converts the intervals to Json. */
	public String prepareIntervals(List<IntervalBase> recordedIntervals) {
		return gson().toJson(recordedIntervals);
	}

	/**
	 * Opens an HTTP connection to the server, and transmits the recorded
	 * intervals with the given user id.
	 */
	public void transferData(String userid, String jsonData) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(buildURL(userid));
		try {
			StringEntity input = new StringEntity(jsonData);
			input.setContentType("application/json");
			post.setEntity(input);

			HttpResponse response = client.execute(post);
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// TODO (MMB) set head pointer in database to new head
				// successful response -- reset
			} else {
				System.out.println(buildURL(userid));
				// transmission to server not successful
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** @return the URL to post new intervals to the server to for this user. */
	private String buildURL(String userid) {
		return WatchDogGlobals.watchDogServerURI + "user/" + userid
				+ "/intervals";
	}
}
