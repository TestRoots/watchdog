package nl.tudelft.watchdog.core.logic.network;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils.Connection;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogUtilsBase;

/**
 * Transmits WatchDog data objects in a Json format to the WatchDog server.
 */
public class JsonTransferer<T extends WatchDogTransferable> {

	/** The {@link GsonBuilder} for building the T's. */
	private GsonBuilder gsonBuilder = new GsonBuilder();

	/** The Gson object for object serialization to Json. */
	private Gson gson;

	/** Constructor. */
	public JsonTransferer() {
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer())
				.registerTypeAdapter(JsonifiedDouble.class, new JsonifiedDoubleSerializer())
				.registerTypeAdapter(JsonifiedLong.class, new JsonifiedLongSerializer());
		gson = gsonBuilder.create();
	}

	/**
	 * Sends the recorded T's to the server. Returns <code>true</code> on
	 * successful transfer, <code>false</code> otherwise.
	 */
	public Connection sendItems(List<T> recordedItems, String projectName) {
		String userId = WatchDogGlobals.getPreferences().getUserId();
		String projectId = WatchDogGlobals.getPreferences().getOrCreateProjectSetting(projectName).projectId;

		// Only transfer if we have both a user id and a project id
		if (WatchDogUtilsBase.isEmptyOrHasOnlyWhitespaces(userId)
				|| WatchDogUtilsBase.isEmptyOrHasOnlyWhitespaces(projectId)) {
			return Connection.UNSUCCESSFUL;
		}

		String serializedItems = toJson(recordedItems);
		try {
			NetworkUtils.transferJsonAndGetResponse(getPostURL(userId, projectId), serializedItems);
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
	public String registerNewUser(User user) throws ServerCommunicationException {
		return registerNew(NetworkUtils.buildNewUserURL(), gson.toJson(user));

	}

	/**
	 * Sends the project registration data and returns the received project-ID.
	 */
	public String registerNewProject(Project project) throws ServerCommunicationException {
		return registerNew(NetworkUtils.buildNewProjectURL(), WatchDogUtilsBase.convertToJson(project));
	}

	/**
	 * Register the new json string with the postURL and reads the response from
	 * the server.
	 * 
	 * @throws ServerCommunicationException
	 */
	public String registerNew(String postURL, String json) throws ServerCommunicationException {
		try {
			String jsonResponse = NetworkUtils.transferJsonAndGetResponse(postURL, json);
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
	public String queryGetURL(String getURL) throws ServerCommunicationException {
		String jsonResponse = NetworkUtils.getURLAndGetResponse(getURL);
		try {
			String response = gson.fromJson(jsonResponse, String.class);

			if (response == null) {
				throw new ServerCommunicationException("Got a null reply from the server.");
			}

			return response;
		} catch (JsonSyntaxException ex) {
			throw new ServerCommunicationException(ex.getMessage());
		}
	}

	/** Converts the items to Json. */
	public String toJson(List<T> recordedItems) {
		try {
			return gson.toJson(recordedItems);
		} catch (RuntimeException e) {
			return "[]";
		}
	}
	
	protected String getPostURL(String userId, String projectId) {
		return null;
	}

	/** A JSon Serializer for Date. */
	private static class DateSerializer implements JsonSerializer<Date> {

		@Override
		public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(date.getTime());
		}
	}

	/** A JSon Serializer for {@link JsonifiedDouble}s. */
	private static class JsonifiedDoubleSerializer implements JsonSerializer<JsonifiedDouble> {

		@Override
		public JsonElement serialize(JsonifiedDouble doubleValue, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(doubleValue.value);
		}
	}

	/** A JSon Serializer for {@link JsonifiedLong}s. */
	private static class JsonifiedLongSerializer implements JsonSerializer<JsonifiedLong> {

		@Override
		public JsonElement serialize(JsonifiedLong longValue, Type type, JsonSerializationContext context) {
			return new JsonPrimitive(longValue.value);
		}
	}

}
