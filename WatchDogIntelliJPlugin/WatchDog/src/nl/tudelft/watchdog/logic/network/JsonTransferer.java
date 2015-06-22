package nl.tudelft.watchdog.logic.network;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import nl.tudelft.watchdog.logic.interval.intervaltypes.IntervalBase;
import nl.tudelft.watchdog.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.Project;
import nl.tudelft.watchdog.ui.wizards.User;

import org.apache.http.HttpEntity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
	public Connection sendIntervals(List<IntervalBase> recordedIntervals) {
		String userid = Preferences.getInstance().getUserid();
		String projectid = UIUtils.getProjectSetting().projectId;
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
			HttpEntity inputStream = NetworkUtils.transferJsonAndGetResponse(
					postURL, json);
			String jsonResponse = NetworkUtils.readResponse(inputStream);
			return gson.fromJson(jsonResponse, String.class);
		} catch (ServerReturnCodeException exception) {
			throw new ServerCommunicationException(exception.getMessage());
		}
	}

	/**
	 * Queries the get URL and reads the response from the server.
	 * 
	 * @throws ServerCommunicationException
	 */
	public String queryGetURL(String getURL)
			throws ServerCommunicationException {
		HttpEntity inputStream = NetworkUtils.getURLAndGetResponse(getURL);
		String jsonResponse = NetworkUtils.readResponse(inputStream);
		return gson.fromJson(jsonResponse, String.class);
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
