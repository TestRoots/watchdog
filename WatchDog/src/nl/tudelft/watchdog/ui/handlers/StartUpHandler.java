package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.logic.IntervalInitializationManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

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
			// initializes the interval manager, and thereby, WatchDog interval
			// recording.
			IntervalInitializationManager.getInstance();
			WatchDogGlobals.isActive = true;
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			ICommandService commandService = (ICommandService) window
					.getService(ICommandService.class);
			if (commandService != null) {
				commandService.refreshElements(
						"nl.tudelft.watchdog.commands.showWatchDogInfo", null);
			}
		} catch (Exception exception) {
			WatchDogLogger.getInstance().logSevere(
					"Caught sever exception on top-level: ");
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}
}
