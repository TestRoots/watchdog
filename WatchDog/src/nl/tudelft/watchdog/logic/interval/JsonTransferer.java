package nl.tudelft.watchdog.logic.interval;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.logic.Project;
import nl.tudelft.watchdog.logic.ServerCommunicationException;
import nl.tudelft.watchdog.logic.User;
import nl.tudelft.watchdog.logic.interval.active.IntervalBase;
import nl.tudelft.watchdog.ui.preferences.Preferences;

import org.apache.http.HttpEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Transmits WatchDog data objects in a Json format to the WatchDog server, e.g.
 * the currently recorded intervals.
 */
public class JsonTransferer {

	/** The {@link GsonBuilder} for building the intervals. */
	private GsonBuilder gsonBuilder = new GsonBuilder();

	/** The Gson object for object serialization to Json. */
	private Gson gson;

	/** Constructor. */
	public JsonTransferer() {
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gson = gsonBuilder.create();
	}

	/** Sends the recorded intervals to the server. */
	public void sendIntervals() {
		List<IntervalBase> recordedIntervals = IntervalManager.getInstance()
				.getClosedIntervals();
		String userid = Preferences.getInstance().getUserid();
		String json = toJson(recordedIntervals);
		try {
			NetworkUtils.transferJson(
					NetworkUtils.buildIntervalsPostURL(userid), json);
		} catch (ServerCommunicationException exception) {
			// TODO (MMB) do not set transfered intervals pointer.
		}
	}

	/**
	 * Sends the user registration data and returns the received User-ID.
	 */
	public String registerNewUser(User user)
			throws ServerCommunicationException {
		return registerNew(NetworkUtils.buildNewUserURL(), gson.toJson(user));

	}

	/**
	 * Sends the project registration data and returns the received project-ID.
	 */
	public String registerNewProject(Project project)
			throws ServerCommunicationException {
		return registerNew(NetworkUtils.buildNewProjectURL(),
				gson.toJson(project));
	}

	/**
	 * Register the new json string with the postURL and reads the response from
	 * the server.
	 * 
	 * @throws ServerCommunicationException
	 */
	public String registerNew(String postURL, String json)
			throws ServerCommunicationException {
		HttpEntity inputStream = NetworkUtils.transferJson(postURL, json);
		String jsonResponse = NetworkUtils.readResponse(inputStream);
		return gson.fromJson(jsonResponse, String.class);
	}

	/** Converts the intervals to Json. */
	/* package */String toJson(List<IntervalBase> recordedIntervals) {
		return gson.toJson(recordedIntervals);
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
