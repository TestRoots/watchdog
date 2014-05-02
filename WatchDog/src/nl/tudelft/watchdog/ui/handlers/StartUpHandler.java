package nl.tudelft.watchdog.ui.handlers;

import java.io.IOException;

import nl.tudelft.watchdog.logic.interval.IntervalManager;
import nl.tudelft.watchdog.logic.logging.WatchDogLogger;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Handler called when an Eclipse instance with the WatchDog plugin installed
 * gets opened and activated.
 */
public class StartUpHandler implements IStartup {

	/** The ui thread for WatchDog registration. */
	private Runnable watchDogUiThread = new StartupUIThread();

	/** {@inheritDoc} Starts the WatchDog plugin. */
	@Override
	public void earlyStartup() {
		watchDogUiThread = new StartupUIThread();
		Display.getDefault().asyncExec(watchDogUiThread);
	}

	/** Starts WatchDog. */
	void startWatchDog() {
		WatchDogGlobals.isActive = true;
		WatchDogLogger.getInstance().logInfo("Starting WatchDog ...");
		IntervalManager.getInstance();
	}

	/**
	 * The UI Thread in which all the registration process on startup of
	 * WatchDog is executed.
	 */
	private class StartupUIThread implements Runnable {
		@Override
		public void run() {
			// the execution strategy is:
			// (1) check user registration (2) wait until 1 is complete, or 1
			// can be skipped (3) show workspace registration
			checkUserRegistration();
			if (!Preferences.getInstance().getUserid().isEmpty()) {
				// In case the user aborted the preference dialog with cancel,
				// we don't want him to have to answer whether he wants WatchDog
				// to be active for this workspace -- it's obvious that he does
				// not want to be bothered for the moment.
				checkWorkspaceRegistration();
			}
			if (Preferences.getInstance().getStore().needsSaving()) {
				try {
					((ScopedPreferenceStore) Preferences.getInstance()
							.getStore()).save();
				} catch (IOException exception) {
				}
			}
		}

		/** Checks whether there is a registered WatchDog user */
		private void checkUserRegistration() {
			if (Preferences.getInstance().getUserid().isEmpty()) {
				UserRegistrationWizardDialogHandler newUserWizardHandler = new UserRegistrationWizardDialogHandler();
				try {
					int statusCode = (int) newUserWizardHandler
							.execute(new ExecutionEvent());
					if (statusCode == Window.CANCEL) {
						MessageDialog
								.openWarning(
										null,
										"WatchDog Warning",
										"WatchDog only works when you register a (possibly anonymous) user.\n\nTakes less than one minute, requires no internet, and you can win prices. As a registered user, you decide on which workspaces WatchDog is active.");
					}
				} catch (ExecutionException exception) {
					WatchDogLogger.getInstance().logInfo(
							"Failed to display register dialog on startup!");
				}
			}
		}

		/**
		 * Checks whether this workspace is registered, and if not, asks the
		 * user whether Watchdog should be active.
		 */
		private void checkWorkspaceRegistration() {
			String workspace = UIUtils.getWorkspaceName();
			if (!Preferences.getInstance().isWorkspaceRegistered(workspace)) {
				boolean useWatchDogInThisWorkspace = MessageDialog
						.openQuestion(null, "WatchDog Workspace Registration",
								"Should WatchDog be active in this workspace?");
				WatchDogLogger.getInstance()
						.logInfo("Registering workspace...");
				Preferences.getInstance().registerWorkspaceUse(workspace,
						useWatchDogInThisWorkspace);
				if (useWatchDogInThisWorkspace) {
					// TODO (MMB) open project registartion wizard
				}
			}

			if (Preferences.getInstance().shouldWatchDogBeActive(workspace)) {
				startWatchDog();
			}
		}

	}
}
