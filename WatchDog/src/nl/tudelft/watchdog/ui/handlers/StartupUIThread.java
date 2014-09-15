package nl.tudelft.watchdog.ui.handlers;

import java.io.IOException;

import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.preferences.WorkspacePreferenceSetting;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.util.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The UI Thread in which all the registration process on startup of
 * WatchDog is executed.
 */
public class StartupUIThread implements Runnable {

	/**
	 * 
	 */
	private final StartUpHandler startUpHandler;
	/** The preferences. */
	private Preferences preferences;

	/** Constructor. */
	public StartupUIThread(StartUpHandler startUpHandler, Preferences preferences) {
		this.startUpHandler = startUpHandler;
		this.preferences = preferences;
	}

	@Override
	public void run() {
		// the execution strategy is:
		// (1) check user registration (2) wait until 1 is complete, or 1
		// can be skipped (3) show workspace registration
		if (WatchDogUtils.isEmpty(preferences.getUserid())) {
			displayUserRegistrationWizard();
		}

		if (!WatchDogUtils.isEmpty(preferences.getUserid())) {
			// In case the user aborted the preference dialog with cancel,
			// we don't want him to have to answer whether he wants WatchDog
			// to be active for this workspace -- it's obvious that he does
			// not want to be bothered for the moment.
			checkWorkspaceRegistration();
			savePreferenceStoreIfNeeded();
		}
	}

	/** Checks whether there is a registered WatchDog user */
	private void displayUserRegistrationWizard() {
		UserRegistrationWizardDialogHandler newUserWizardHandler = new UserRegistrationWizardDialogHandler();
		try {
			int statusCode = (int) newUserWizardHandler
					.execute(new ExecutionEvent());
			if (statusCode == Window.CANCEL) {
				MessageDialog.openWarning(null, "WatchDog not active!",
						UIUtils.WATCHDOG_WARNING);
			}
		} catch (ExecutionException exception) {
			// when the new user wizard cannot be displayed, new
			// users cannot register with WatchDog.
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}

	/**
	 * Checks whether this workspace is registered, and if not, asks the
	 * user whether WatchDog should be active.
	 */
	private void checkWorkspaceRegistration() {
		String workspaceName = UIUtils.getWorkspaceName();
		checkIsWorkspaceAlreadyRegistered(workspaceName);
		checkWhetherToStartWatchDog(workspaceName);
	}

	private void checkIsWorkspaceAlreadyRegistered(String workspace) {
		if (!preferences.isWorkspaceRegistered(workspace)) {
			boolean useWatchDogInThisWorkspace = MessageDialog
					.openQuestion(null, "WatchDog Workspace Registration",
							"Should WatchDog be active in this workspace?");
			WatchDogLogger.getInstance()
					.logInfo("Registering workspace...");
			preferences.registerWorkspaceUse(workspace,
					useWatchDogInThisWorkspace);
		}
	}

	private void checkWhetherToStartWatchDog(String workspaceName) {
		WorkspacePreferenceSetting setting = preferences
				.getOrCreateWorkspaceSetting(workspaceName);
		if (setting.enableWatchdog) {
			if (WatchDogUtils.isEmpty(setting.projectId)) {
				displayProjectWizard();
			}
			savePreferenceStoreIfNeeded();

			// reload setting from preferences
			setting = preferences
					.getOrCreateWorkspaceSetting(workspaceName);
			if (!WatchDogUtils.isEmpty(setting.projectId)) {
				this.startUpHandler.startWatchDog();
			}
		}
	}

	private void displayProjectWizard() {
		ProjectRegistrationWizardDialogHandler newProjectWizardHandler = new ProjectRegistrationWizardDialogHandler();
		try {
			newProjectWizardHandler.execute(new ExecutionEvent());
		} catch (ExecutionException exception) {
			// when the new project wizard cannot be displayed, new
			// users cannot register with WatchDog.
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}

	private void savePreferenceStoreIfNeeded() {
		if (preferences.getStore().needsSaving()) {
			try {
				((ScopedPreferenceStore) preferences.getStore()).save();
			} catch (IOException exception) {
				WatchDogLogger.getInstance().logSevere(exception);
			}
		}
	}
}