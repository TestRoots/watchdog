package nl.tudelft.watchdog.interval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.watchdog.gui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.interval.recorded.IInterval;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

/**
 * Transmits the currently recorded intervals to the WatchDog server.
 */
public class IntervalTransferer {

	private void sendIntervals() {
		List<IInterval> recordedIntervals = IntervalManager.getInstance()
				.getRecordedIntervals();
		String userid = WatchdogPreferences.getUserid();
		String json = prepareIntervals(recordedIntervals);
		transferData(userid, json);
	}

	/** Converts the intervals to Json. */
	public String prepareIntervals(List<IInterval> recordedIntervals) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		return gson.toJson(recordedIntervals);
	}

	/**
	 * Opens an HTTP connection to the server, and transmits the recorded
	 * intervals with the given user id.
	 */
	public void transferData(String userid, String json) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(WatchDogGlobals.watchDogServer
				+ "intervals/");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("json", json));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			client.execute(post);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
