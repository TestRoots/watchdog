package nl.tudelft.watchdog.logic.interval;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/** Transmits the currently recorded intervals to the WatchDog server. */
public class IntervalTransferer {

	/** The {@link GsonBuilder} for building the intervals. */
	private GsonBuilder gsonBuilder = new GsonBuilder();

	/** The Gson object for object serialization to Json. */
	private Gson gson;

	/** Constructor. */
	public IntervalTransferer() {
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gson = gsonBuilder.create();
	}

	/** Sends the recorded intervals to the server. */
	public void sendIntervals() {
		List<IntervalBase> recordedIntervals = IntervalManager.getInstance()
				.getRecordedIntervals();
		String userid = WatchdogPreferences.getInstance().getUserid();
		String json = toJson(recordedIntervals);
		transferJson(buildURL(userid), json);
	}

	/** Converts the intervals to Json. */
	public String toJson(List<IntervalBase> recordedIntervals) {
		return gson.toJson(recordedIntervals);
	}

	/**
	 * Opens an HTTP connection to the server, and transmits the supplied json
	 * data to the server. In case of error, the exact problem is logged.
	 */
	public void transferJson(String url, String jsonData) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		try {
			StringEntity input = new StringEntity(jsonData);
			input.setContentType("application/json");
			post.setEntity(input);

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				// TODO (MMB) set head pointer in database to new head
				// successful response -- reset
			} else {
				// transmission to server not successful
				WatchDogLogger.getInstance().logInfo(
						"Failed to post intervals to server. Status code: "
								+ response.getStatusLine().getStatusCode()
								+ " Message: "
								+ EntityUtils.toString(response.getEntity()));

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

	/** A JSon Serializer for Date. */
	private class DateSerializer implements JsonSerializer<Date> {

		@Override
		public JsonElement serialize(Date date, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
	}
}
