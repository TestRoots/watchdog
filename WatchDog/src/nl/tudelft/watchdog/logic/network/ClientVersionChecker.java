package nl.tudelft.watchdog.logic.network;

import java.util.Timer;
import java.util.TimerTask;

import nl.tudelft.watchdog.logic.exceptions.ServerCommunicationException;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

/**
 * This manager repeatedly checks (every {@value #UPDATE_RATE} minutes if a new
 * version of the WatchDog client is available and sets the preferences
 * accordingly.
 */
public class ClientVersionChecker {

	private static int UPDATE_RATE = 15 * 60 * 1000;

	private Timer timer;

	private ClientVersionCheckerTimerTask task;

	/**
	 * Constructor. Tries to immediately transfer all remaining intervals, and
	 * sets up a scheduled timer to run every {@value #UPDATE_RATE}
	 * milliseconds.
	 */
	public ClientVersionChecker() {
		task = new ClientVersionCheckerTimerTask();
		task.run();
		timer = new Timer(true);
		timer.scheduleAtFixedRate(task, 0, UPDATE_RATE);
	}

	private static class ClientVersionCheckerTimerTask extends TimerTask {
		private Preferences preferences;
		private JsonTransferer jsonTransferer = new JsonTransferer();

		private ClientVersionCheckerTimerTask() {
			this.preferences = Preferences.getInstance();
		}

		/**
		 * Transfers all intervals from the persistence storage that are not yet
		 * on the server, to the server.
		 */
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
				UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
			} catch (ServerCommunicationException exception) {
			}
		}
	}
}
