package nl.tudelft.watchdog.core.logic.network;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;

/**
 * Utility functions for accessing the network.
 */
public class NetworkUtils {
	
	/**
	 * Connection timeout to be used by the HttpClient.
	 * Default timeout is 12 seconds. 
	 */	
	public static final int DEFAULT_TIMEOUT = 12*1000;
	private static int connectionTimeout = DEFAULT_TIMEOUT;
	
	/** A handle to the current Http client */ 
	private static CloseableHttpClient client = null;

	/**
	 * An enum denoting the three possible different connection outcomes:
	 * successful, unsuccessful, or a network error.
	 */
	public enum Connection {
		/**
		 * Server returned expected answer.
		 */
		SUCCESSFUL,
		/**
		 * Server returned something, but not the expected answer.
		 */
		UNSUCCESSFUL,
		/**
		 * Network error. Can be temporary.
		 */
		NETWORK_ERROR
	};
	
	/**
	 * Change the default value of the connection timeout to the specified one.
	 */
	public static void setConnectionTimeout(int timeout) {
		connectionTimeout = timeout;
	}
	
	/**
	 * Cancels the current request after the specified duration
	 */
	public static void cancelTransferAfter(long milliseconds){
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				closeHttpClientGracefully(client);
			}
		};
		timer.schedule(task, milliseconds);
	}

	/**
	 * Returns the content at the given URL.
	 *
	 * @throws ServerCommunicationException
	 */
	public static String getURLAndGetResponse(String url)
			throws ServerCommunicationException {
		client = createHTTPClient();
		HttpGet get;
		String errorMessage = "";

		try {
			get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				String jsonResponse = readResponse(response.getEntity());
				return jsonResponse;
			} else {
				errorMessage = "Not received " + HttpStatus.SC_OK;
			}
		} catch (IllegalStateException exception) {
			// intentionally empty
		} catch (IllegalArgumentException exception) {
			// intentionally empty
		} catch (IOException exception) {
			// intentionally empty
		} finally {
			closeHttpClientGracefully(client);
		}
		throw new ServerCommunicationException(errorMessage);
	}

	/**
	 * Checks whether the given url is reachable and exists.
	 *
	 * @return a {@link Connection} object depicting how the connection went.
	 */
	public static Connection urlExistsAndReturnsStatus200(String url) {
		client = createHTTPClient();
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
		} catch (IllegalStateException exception) {
			// intentionally empty
		} catch (IOException exception) {
			// intentionally empty
		} finally {
			closeHttpClientGracefully(client);
		}
		return Connection.NETWORK_ERROR;
	}

	private static void closeHttpClientGracefully(CloseableHttpClient client) {		
		if (client == null) return;
		
		try {
			client.close();
		} catch (IOException exception) {
			// intentionally empty
		}		
	}

	/**
	 * Opens an HTTP connection to the server, and transmits the supplied json
	 * data to the server. In case of error, the exact problem is logged.
	 *
	 * @return The json from the response.
	 * @throws ServerCommunicationException
	 * @throws ServerReturnCodeException
	 */
	public static String transferJsonAndGetResponse(String url, String jsonData)
			throws ServerCommunicationException, ServerReturnCodeException {
		client = createHTTPClient();
		HttpPost post = new HttpPost(url);
		String errorMessage = "";

		try {
			StringEntity input = new StringEntity(jsonData);
			WatchDogLogger.getInstance().logInfo(
					"Data length: " + ((double) input.getContentLength())
							/ 1024 + " kB");
			input.setContentType("application/json");
			post.setEntity(input);

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				String jsonResponse = readResponse(response.getEntity());
				return jsonResponse;
			} else {
				// server returns not created
				throw new ServerReturnCodeException(
						"Failed to execute request on server (status code: "
								+ response.getStatusLine().getStatusCode()
								+ "). " + readResponse(response.getEntity()));
			}
		} catch (IOException e) {
			// server unreachable case
			errorMessage = "Failed to communicate with our server: " + e + " "
					+ e.getMessage();
		} catch (IllegalStateException e) {
			// URL wrongly formatted (target host is null)
			errorMessage = "URL wrongly formatted. " + e.getMessage();
		} finally {
			closeHttpClientGracefully(client);
		}
		WatchDogLogger.getInstance().logInfo(errorMessage);
		throw new ServerCommunicationException(errorMessage);
	}

	/**
	 * @return the URL for client query.
	 */
	public static String buildClientURL() {
		return getServerURI() + "client";
	}

	/**
	 * @return the base URL for new user registration.
	 */
	public static String buildNewUserURL() {
		return getServerURI() + "user";
	}

	/**
	 * @return the base URL for new user registration.
	 */
	public static String buildNewProjectURL() {
		return getServerURI() + "project";
	}

	/**
	 * @return the base URL for (existing) user-based operations.
	 */
	public static String buildExistingUserURL(String id) {
		return getServerURI() + "user/" + id;
	}

	/**
	 * @return the base URL for user-based operations.
	 */
	public static String buildExistingProjectURL(String id) {
		return getServerURI() + "project/" + id;
	}

	/**
	 * @return the URL to post new intervals to the server to for this user.
	 */
	public static String buildIntervalsPostURL(String userid, String projectid) {
		return buildExistingUserURL(userid) + "/" + projectid + "/intervals";
	}
	
	/**
	 * @return the URL to post new events to the server to for this user.
	 */
	public static String buildEventsPostURL(String userid, String projectid) {
		return buildExistingUserURL(userid) + "/" + projectid + "/events";
	}

	/**
	 * Wrapper function for Appache's {@link EntityUtils#toString()}, taking
	 * care of the exceptions (which should never happen).
	 */
	public static String readResponse(HttpEntity entity) {
		try {
			return EntityUtils.toString(entity);
		} catch (ParseException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		} catch (IOException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}
		return "";
	}

	private static String getServerURI() {
		return WatchDogGlobals.getPreferences().getServerURI();
	}

	/**
	 * Builds the correct HTTPClient according to the Preferences.
	 */
	private static CloseableHttpClient createHTTPClient() {
		if (WatchDogGlobals.getPreferences().isAuthenticationEnabled()) {
			return createAuthenticatedHttpClient();
		}
		return createNormalHttpClient();
	}

	/**
	 * Creates an HTTP client that uses a normal connection.
	 */
	private static CloseableHttpClient createNormalHttpClient() {
		return createPlainHttpClientBuilder().build();
	}

	/**
	 * Creates a vanilla HTTP client builder with some timeouts.
	 */
	private static HttpClientBuilder createPlainHttpClientBuilder() {
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(connectionTimeout)
				.setConnectTimeout(connectionTimeout)
				.setSocketTimeout(connectionTimeout).build();
		return HttpClientBuilder.create().setDefaultRequestConfig(config);
	}

	/**
	 * Creates an HTTP client that uses an authenticated connection.
	 */
	private static CloseableHttpClient createAuthenticatedHttpClient() {
		CredentialsProvider provider = new BasicCredentialsProvider();
		byte[] providerInfo = { 104, 110, 115, 112, 113, 115, 122, 110, 112,
				113 };
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"watchdogplugin", new String(providerInfo,
						Charset.defaultCharset()));
		provider.setCredentials(AuthScope.ANY, credentials);
		return createPlainHttpClientBuilder().setDefaultCredentialsProvider(
				provider).build();
	}

}
