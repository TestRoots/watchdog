package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
		WatchDogGlobals.isActive = true;
		WatchDogLogger.getInstance().logInfo("Starting WatchDog ...");
		IntervalManager.getInstance();

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (WatchdogPreferences.getInstance().getUserid().isEmpty()) {
					// if the UserID is empty, show WatchDog registration
					// dialog.
					UserWizardDialogHandler newUserWizardHandler = new UserWizardDialogHandler();
					try {
						newUserWizardHandler.execute(new ExecutionEvent());
					} catch (ExecutionException exception) {
						// TODO (MMB) Add a warning to the user here to manually
						// execute registering?
						WatchDogLogger
								.getInstance()
								.logInfo(
										"Failed to display register dialog on startup!");
					}
				}
			}
		});
	}
}
