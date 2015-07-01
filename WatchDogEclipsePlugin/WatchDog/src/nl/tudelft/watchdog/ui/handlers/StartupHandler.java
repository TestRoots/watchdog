package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogGlobals.IDE;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.ui.WatchDogView;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

/**
 * Handler called when an Eclipse instance with the WatchDog plugin installed
 * gets opened and activated.
 */
public class StartupHandler implements IStartup {

	/** {@inheritDoc} Starts the WatchDog plugin. */
	@Override
	public void earlyStartup() {
		WatchDogGlobals.logDirectory = "watchdog/logs/";
		WatchDogGlobals.preferences = Preferences.getInstance();
		StartupUIThread watchDogUiThread = new StartupUIThread(
				Preferences.getInstance());
		Display.getDefault().asyncExec(watchDogUiThread);
	}

	/** Starts WatchDog. */
	public static void startWatchDog() {
		try {
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logInfo(
					"Starting WatchDog ...");

			WatchDogGlobals.hostIDE = IDE.ECLIPSE;
			// Initialize the interval manager, and thereby, interval recording.
			InitializationManager.getInstance();
			WatchDogGlobals.isActive = true;
			// Update WatchDog icon
			UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
			updateView();
		} catch (Exception exception) {
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logSevere(
					"Caught sever exception on top-level: ");
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logSevere(
					exception);
		}
	}

	private static void updateView() {
		WatchDogView view = UIUtils.getWatchDogView();
		if (view != null) {
			view.update();
		}
	}
}
