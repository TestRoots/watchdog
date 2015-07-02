package nl.tudelft.watchdog.logic.network;

import java.util.TimerTask;
import java.util.regex.Pattern;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.logic.ui.RegularCheckerBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;

/**
 * This manager repeatedly checks (every {@value #UPDATE_RATE} minutes if a new
 * version of the WatchDog client is available and sets the preferences
 * accordingly.
 */
public class ClientVersionChecker extends RegularCheckerBase {

	private static final int UPDATE_RATE = 15 * 60 * 1000;

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public ClientVersionChecker() {
		super(UPDATE_RATE);
		task = new ClientVersionCheckerTimerTask();
		runSetupAndStartTimeChecker();
	}

	private static class ClientVersionCheckerTimerTask extends TimerTask {
		private final Preferences preferences;
		private final JsonTransferer jsonTransferer = new JsonTransferer();

		private ClientVersionCheckerTimerTask() {
			this.preferences = Preferences.getInstance();
		}

		/** Performs version comparison. */
		@Override
		public void run() {
			try {
				String currentServerVersion = jsonTransferer
						.queryGetURL(NetworkUtils.buildClientURL());
				if (!currentServerVersion
						.equals(WatchDogGlobals.CLIENT_VERSION)) {
					preferences.setIsOldVersion(true);
				} else {
					preferences.setIsOldVersion(false);
				}

				preferences.setBigUpdateAvailable(hasMajorOrMinorVersionGap(
						currentServerVersion, WatchDogGlobals.CLIENT_VERSION));

				UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
			} catch (ServerCommunicationException exception) {
				// intentionally empty
			}
		}
	}

	/**
	 * @return <code>true</code> if the two supplied strings have a difference
	 *         in their major or minor version. <code>false</code> otherwise.
	 */
	protected static boolean hasMajorOrMinorVersionGap(String version1,
			String version2) {
		String regEx = Pattern.quote(".");
		String[] majorMinorPatchVersionServer = version1.split(regEx);
		String[] majorMinorPatchVersionLocal = version2.split(regEx);

		if (!majorMinorPatchVersionLocal[0]
				.equals(majorMinorPatchVersionServer[0])
				|| !majorMinorPatchVersionLocal[1]
						.equals(majorMinorPatchVersionServer[1])) {
			return true;
		}
		return false;
	}
}
