package nl.tudelft.watchdog.logic;

import java.io.IOException;

import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/** Utility functions for accessing the network. */
public class NetworkUtils {

	/**
	 * An enum denoting the three possible different connection outcomes:
	 * successful, unsuccessful, or a network error.
	 */
	public enum Connection {
		/** Server returned expected answer. */
		SUCCESSFUL,
		/** Server returned something, but not the expected answer. */
		UNSUCCESSFUL,
		/** Network error. */
		NETWORK_ERROR
	};

	/**
	 * Checks whether the given url is reachable and exists. The connection
	 * timeout is set to 5 seconds.
	 * 
	 * @return if the url exists and returns a 200 status code.
	 *         <code>false</code> if the url does not return 200. In case of
	 *         NetworkFailure, throws an execpetion.
	 */
	public static Connection urlExistsAndReturnsStatus200(String url) {
		int connectionTimeout = 5000;
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(connectionTimeout)
				.setConnectTimeout(connectionTimeout)
				.setSocketTimeout(connectionTimeout).build();
		CredentialsProvider provider = new BasicCredentialsProvider();
		byte[] password = { 104, 110, 115, 112, 113, 115, 122, 110, 112, 113 };
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"watchdogplugin", new String(password));
		provider.setCredentials(AuthScope.ANY, credentials);
		HttpClient client = HttpClientBuilder.create()
				.setDefaultRequestConfig(config)
				.setDefaultCredentialsProvider(provider).build();

		HttpGet get;
		try {
			get = new HttpGet(url);
		} catch (IllegalArgumentException e) {
			return Connection.UNSUCCESSFUL;
		}
		try {
			HttpResponse response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return Connection.SUCCESSFUL;
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				return Connection.UNSUCCESSFUL;
			}
			return Connection.NETWORK_ERROR;
		} catch (IOException exception) {
			// intentionally empty
		}
		// TODO (MMB) throw network access fail exception
		// this return is present just to fulfill the method requirements.
		return Connection.NETWORK_ERROR;
	}

	/**
	 * Opens an HTTP connection to the server, and transmits the supplied json
	 * data to the server. In case of error, the exact problem is logged.
	 */
	public static void transferJson(String url, String jsonData) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		boolean stored = false;
		try {
			StringEntity input = new StringEntity(jsonData);
			input.setContentType("application/json");
			post.setEntity(input);

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				// TODO (MMB) set head pointer in database to new head
				// successful response -- reset
				stored = true;
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
		if (!stored) {
			// TODO (MMB) throw storage failed exception, make function throw
			// this exception and handle in calling function
		}
	}

	/** @return the base URL for user-based operations. */
	public static String buildUserURL(String id) {
		return WatchDogGlobals.watchDogServerURI + "user/" + id;
	}

	/** @return the base URL for user-based operations. */
	public static String buildProjectURL(String id) {
		return WatchDogGlobals.watchDogServerURI + "project/" + id;
	}

	/** @return the URL to post new intervals to the server to for this user. */
	public static String buildIntervalsPostURL(String userid) {
		return buildUserURL(userid) + "/intervals";
	}
}
