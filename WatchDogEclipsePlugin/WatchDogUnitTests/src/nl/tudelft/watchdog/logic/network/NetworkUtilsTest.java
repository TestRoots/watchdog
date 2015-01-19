package nl.tudelft.watchdog.logic.network;

import nl.tudelft.watchdog.ui.preferences.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Preferences.class)
// Used to ignore the SSLCOntext loading problems stemming from the upstream
// class loader
@PowerMockIgnore("javax.net.ssl.*")
public class NetworkUtilsTest {

	@org.mockito.Mock
	Preferences mockedPreferences;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(Preferences.class);
		Mockito.when(Preferences.getInstance()).thenReturn(mockedPreferences);
		Mockito.when(mockedPreferences.isAuthenticationEnabled()).thenReturn(
				true);
	}

	@Test(expected = ServerCommunicationException.class)
	public void testTransferToEmptyURLBug148()
			throws ServerCommunicationException, ServerReturnCodeException {
		NetworkUtils.transferJsonAndGetResponse("", "");
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
