package nl.tudelft.watchdog.logic.network;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.logic.network.ServerReturnCodeException;
import org.junit.Test;

public class NetworkUtilsTest {

	@Test(expected = ServerCommunicationException.class)
	public void can_handle_empty_url()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("", "");
	}

	@Test(expected = ServerCommunicationException.class)
	public void can_handle_just_one_slash()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("/", "");
	}

	@Test(expected = ServerCommunicationException.class)
	public void can_handle_missing_protocol()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("watchdog.testroots.org", "");
	}

	@Test(expected = ServerReturnCodeException.class)
	public void can_handle_non_existing_server() throws ServerCommunicationException,
			ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("http://google.de", "");
	}

}
