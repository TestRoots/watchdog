package nl.tudelft.watchdog.logic.network;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.logic.network.ServerReturnCodeException;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;

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
@PrepareForTest(WatchDogGlobals.class)
// Used to ignore the SSLContext loading problems stemming from the upstream
// class loader
@PowerMockIgnore("javax.net.ssl.*")
public class NetworkUtilsTest {

	@org.mockito.Mock
	Preferences mockedPreferences;
	
	@org.mockito.Mock
	WatchDogGlobals mockedGlobals;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(WatchDogGlobals.class);
		Mockito.when(WatchDogGlobals.getLogDirectory()).thenReturn("watchdog/logs/");
		Mockito.when(WatchDogGlobals.getPreferences()).thenReturn(mockedPreferences);
		Mockito.when(mockedPreferences.isAuthenticationEnabled()).thenReturn(
				true);
		Mockito.when(mockedPreferences.isLoggingEnabled()).thenReturn(false);
	}

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
