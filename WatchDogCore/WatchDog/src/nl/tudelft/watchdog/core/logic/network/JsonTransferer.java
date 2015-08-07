package nl.tudelft.watchdog.core.logic.network;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.core.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

/**
 * Transmits WatchDog data objects in a Json format to the WatchDog server.
 */
public class JsonTransferer {

	/** The {@link GsonBuilder} for building the intervals. */
	private GsonBuilder gsonBuilder = new GsonBuilder();

	/** The Gson object for object serialization to Json. */
	private Gson gson;

	/** Constructor. */
	public JsonTransferer() {
		gsonBuilder
				.registerTypeAdapter(Date.class, new DateSerializer())
				.registerTypeAdapter(JsonifiedDouble.class,
						new JsonifiedDoubleSerializer())
				.registerTypeAdapter(JsonifiedLong.class,
						new JsonifiedLongSerializer());
		gson = gsonBuilder.create();
	}

	/**
	 * Sends the recorded intervals to the server. Returns <code>true</code> on
	 * successful transfer, <code>false</code> otherwise.
	 */
	public Connection sendIntervals(List<IntervalBase> recordedIntervals, String projectName) {
		String userid = WatchDogGlobals.preferences.getUserid();
		String projectid = WatchDogGlobals.preferences.getOrCreateProjectSetting(projectName).projectId;
		String json = toJson(recordedIntervals);
		try {
			NetworkUtils
					.transferJsonAndGetResponse(NetworkUtils
							.buildIntervalsPostURL(userid, projectid), json);
			return Connection.SUCCESSFUL;
		} catch (ServerReturnCodeException exception) {
			return Connection.UNSUCCESSFUL;
		} catch (ServerCommunicationException exception) {
			return Connection.NETWORK_ERROR;
		} catch (IllegalArgumentException exception) {
			return Connection.NETWORK_ERROR;
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
	 * @throws
	 */
	public String registerNew(String postURL, String json)
			throws ServerCommunicationException {
		try {
			String jsonResponse = NetworkUtils.transferJsonAndGetResponse(
					postURL, json);
			return gson.fromJson(jsonResponse, String.class);
		} catch (ServerReturnCodeException exception) {
			throw new ServerCommunicationException(exception.getMessage());
		}
	}

	/**
	 * Queries the URL via GET, reads and returns the response from the server.
	 * 
	 * @throws ServerCommunicationException
	 */
	public String queryGetURL(String getURL)
			throws ServerCommunicationException {
		String jsonResponse = NetworkUtils.getURLAndGetResponse(getURL);
		try {
			String response = gson.fromJson(jsonResponse, String.class);

			if (response == null) {
				throw new ServerCommunicationException(
						"Got a null reply from the server.");
			}

			return response;
		} catch (JsonSyntaxException ex) {
			throw new ServerCommunicationException(ex.getMessage());
		}
	}

	/** Converts the intervals to Json. */
	public String toJson(List<IntervalBase> recordedIntervals) {
		try {
			return gson.toJson(recordedIntervals);
		} catch (RuntimeException e) {
			return "[]";
		}
	}

	/** A JSon Serializer for Date. */
	private static class DateSerializer implements JsonSerializer<Date> {

		@Override
		public JsonElement serialize(Date date, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
	}

	/** A JSon Serializer for {@link JsonifiedDouble}s. */
	private static class JsonifiedDoubleSerializer implements
			JsonSerializer<JsonifiedDouble> {

		@Override
		public JsonElement serialize(JsonifiedDouble doubleValue, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(doubleValue.value);
		}
	}

	/** A JSon Serializer for {@link JsonifiedLong}s. */
	private static class JsonifiedLongSerializer implements
			JsonSerializer<JsonifiedLong> {

		@Override
		public JsonElement serialize(JsonifiedLong longValue, Type type,
				JsonSerializationContext context) {
			return new JsonPrimitive(longValue.value);
		}
	}

}
