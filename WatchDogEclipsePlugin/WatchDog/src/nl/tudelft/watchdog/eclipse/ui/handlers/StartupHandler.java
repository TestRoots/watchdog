package nl.tudelft.watchdog.eclipse.ui.handlers;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogGlobals.IDE;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.logic.InitializationManager;
import nl.tudelft.watchdog.eclipse.ui.WatchDogView;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;

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
		WatchDogGlobals.setLogDirectory("watchdog/logs/");
		WatchDogGlobals.setPreferences(Preferences.getInstance());
		WatchDogGlobals.hostIDE = IDE.ECLIPSE;
		StartupUIThread watchDogUiThread = new StartupUIThread(
				Preferences.getInstance());
		Display.getDefault().asyncExec(watchDogUiThread);
	}

	/** Starts WatchDog. */
	public static void startWatchDog() {
		try {
			WatchDogLogger.getInstance().logInfo("Starting WatchDog ...");

			// Initialize the interval manager, and thereby, interval recording.
			InitializationManager.getInstance();
			WatchDogGlobals.isActive = true;
			// Update WatchDog icon
			UIUtils.refreshCommand(UIUtils.COMMAND_SHOW_INFO);
			updateView();
		} catch (Exception exception) {
			WatchDogLogger.getInstance().logSevere(
					"Caught sever exception on top-level: ");
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}

	private static void updateView() {
		WatchDogView view = UIUtils.getWatchDogView();
		if (view != null) {
			view.update();
		}
	}
}
