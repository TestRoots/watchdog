package nl.tudelft.watchdog.logic.network;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.tudelft.watchdog.core.logic.storage.WatchDogItem;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogGlobals.IDE;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * Test the transfer from {@link WatchDogItem}s to JSon.
 */
public abstract class JsonConverterTestBase {

	@BeforeClass
	public static void setUp() {
		WatchDogGlobals.hostIDE = IDE.ECLIPSE;
	}

	protected String pasteWDVAndClient() {
		return "\"wdv\":\"" + WatchDogGlobals.CLIENT_VERSION + "\",\"ide\":\"ec\"";
	}

	@Test
	public void testUserHasWatchDogVersion() {
		String gsonRepresentation = WatchDogUtils.convertToJson(new User());
		boolean containsWDVersion = gsonRepresentation.contains("\"wdv\":\"" + WatchDogGlobals.CLIENT_VERSION + "\"");
		assertTrue(containsWDVersion);
	}

	@Test
	public void testProjectHasWatchDogVersion() {
		String gsonRepresentation = WatchDogUtils.convertToJson(new Project(""));
		boolean containsWDVersion = gsonRepresentation.contains("\"wdv\":\"" + WatchDogGlobals.CLIENT_VERSION + "\"");
		assertTrue(containsWDVersion);
	}

}
