package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.ui.WatchDogView;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;
import nl.tudelft.watchdog.util.WatchDogLogger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;

/**
 * Handler called when an Eclipse instance with the WatchDog plugin installed
 * gets opened and activated.
 */
public class StartUpHandler implements IStartup {

	/** {@inheritDoc} Starts the WatchDog plugin. */
	@Override
	public void earlyStartup() {
		StartupUIThread watchDogUiThread = new StartupUIThread(this,
				Preferences.getInstance());
		Display.getDefault().asyncExec(watchDogUiThread);
	}

	/** Starts WatchDog. */
	/* package */void startWatchDog() {
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

	private void updateView() {
		WatchDogView view = UIUtils.getWatchDogView();
		if (view != null) {
			view.update();
		}
	}
}
