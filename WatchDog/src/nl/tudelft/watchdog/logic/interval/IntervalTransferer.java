package nl.tudelft.watchdog.logic.interval;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.recorded.RecordedInterval;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Transmits the currently recorded intervals to the WatchDog server.
 */
public class IntervalTransferer {

	/** Sends the recorded intervals to the server. */
	public void sendIntervals() {
		List<RecordedInterval> recordedIntervals = IntervalManager
				.getInstance().getRecordedIntervals();
		String userid = WatchdogPreferences.getUserid();
		String json = prepareIntervals(recordedIntervals);
		transferData(userid, json);
	}

	/** Converts the intervals to Json. */
	public String prepareIntervals(List<RecordedInterval> recordedIntervals) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		Gson gson = gsonBuilder.create();
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

	/** A JSon serializer for Date. */
	private class DateSerializer implements JsonSerializer<Date> {

		@Override
		public JsonElement serialize(Date date, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
	}
}
