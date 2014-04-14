package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.WatchdogPreferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
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

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				checkUserRegistration();
				checkWorkspaceRegistration();
			}

			private void checkWorkspaceRegistration() {
				String workspace = ResourcesPlugin.getWorkspace().getRoot()
						.getLocation().toFile().toString();
				if (!WatchdogPreferences.getInstance().isWorkspaceRegistered(
						workspace)) {
					MessageDialog dialog = new MessageDialog(null, "WatchDog",
							null, "Should WatchDog monitor this workspace?",
							MessageDialog.QUESTION,
							new String[] { "Yes", "No" }, 0);
					boolean useWatchDogInThisWorkspace = (dialog.open() == 0) ? true
							: false;
					WatchDogLogger.getInstance().logInfo(
							"Registering workspace...");
					WatchdogPreferences.getInstance().registerWorkspace(
							workspace, useWatchDogInThisWorkspace);
				}

				if (WatchdogPreferences.getInstance().shouldWatchDogBeActive(
						workspace)) {
					startWatchDog();
				}
			}

			private void checkUserRegistration() {
				if (WatchdogPreferences.getInstance().getUserid().isEmpty()) {
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

	/** Starts WatchDog. */
	void startWatchDog() {
		WatchDogGlobals.isActive = true;
		WatchDogLogger.getInstance().logInfo("Starting WatchDog ...");
		IntervalManager.getInstance();
	}
}
