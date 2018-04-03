package nl.tudelft.watchdog.logic.network;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.logic.network.ServerReturnCodeException;
import org.junit.Test;

public class NetworkUtilsTest {

	@Test(expected = ServerCommunicationException.class)
	public void testTransferToEmptyURLBug148()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("", "");
	}
	
	@Test(expected = ServerCommunicationException.class)
	public void testTransferToSlashURLBug148()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("/", "");
	}

	@Test(expected = ServerCommunicationException.class)
	public void testTransferToInvalidURLBug148()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("watchdog.testroots.org", "");
	}

	@Test(expected = ServerReturnCodeException.class)
	public void testTransferToValidURL() throws ServerCommunicationException,
			ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("http://google.de", "");
	}

}
