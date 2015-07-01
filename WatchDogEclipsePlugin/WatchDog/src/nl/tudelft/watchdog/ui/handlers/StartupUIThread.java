package nl.tudelft.watchdog.ui.handlers;

import java.io.IOException;

import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The UI Thread in which all the registration process on startup of WatchDog is
 * executed.
 */
public class StartupUIThread implements Runnable {

	/** The warning displayed when WatchDog is not active. */
	public static final String WATCHDOG_INACTIVE_WARNING = "WatchDog only works when you register a (possibly anonymous) user and project.\n\nTakes less than one minute,  and you can win prices. As a registered user, you decide where WatchDog is active.";

	/** The preferences. */
	private Preferences preferences;

	/** Whether the user has cancelled the user project registration wizard. */
	private boolean userProjectRegistrationCancelled = false;

	private String workspaceName;

	/** Constructor. */
	public StartupUIThread(Preferences preferences) {
		this.preferences = preferences;
		this.workspaceName = WatchDogUtils.getWorkspaceName();
	}

	@Override
	public void run() {
		checkWhetherToDisplayUserProjectRegistrationWizard();
		savePreferenceStoreIfNeeded();

		if (WatchDogUtils.isEmpty(preferences.getUserid())
				|| userProjectRegistrationCancelled) {
			return;
		}

		checkIsWorkspaceAlreadyRegistered();
		checkWhetherToDisplayProjectWizard();
		checkWhetherToStartWatchDog();
	}

	/** Checks whether there is a registered WatchDog user */
	private void checkWhetherToDisplayUserProjectRegistrationWizard() {
		if (!WatchDogUtils.isEmpty(preferences.getUserid()))
			return;

		UserRegistrationWizardDialogHandler newUserWizardHandler = new UserRegistrationWizardDialogHandler();
		try {
			int statusCode = (int) newUserWizardHandler
					.execute(new ExecutionEvent());
			if (statusCode == Window.CANCEL) {
				MessageDialog.openWarning(null, "WatchDog not active!",
						WATCHDOG_INACTIVE_WARNING);
				userProjectRegistrationCancelled = true;
			}
		} catch (ExecutionException exception) {
			// when the new user wizard cannot be displayed, new
			// users cannot register with WatchDog.
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logSevere(
					exception);
		}
	}

	private void checkIsWorkspaceAlreadyRegistered() {
		if (!preferences.isProjectRegistered(workspaceName)) {
			boolean useWatchDogInThisWorkspace = MessageDialog.openQuestion(
					null, "WatchDog Workspace Registration",
					"Should WatchDog be active in this workspace?");
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logInfo(
					"Registering workspace...");
			preferences.registerProjectUse(workspaceName,
					useWatchDogInThisWorkspace);
		}
	}

	private void checkWhetherToDisplayProjectWizard() {
		ProjectPreferenceSetting setting = preferences
				.getOrCreateProjectSetting(workspaceName);
		if (setting.enableWatchdog && WatchDogUtils.isEmpty(setting.projectId)) {
			displayProjectWizard();
			savePreferenceStoreIfNeeded();
		}
	}

	private void checkWhetherToStartWatchDog() {
		// reload setting from preferences
		ProjectPreferenceSetting setting = preferences
				.getOrCreateProjectSetting(workspaceName);
		if (setting.enableWatchdog) {
			StartupHandler.startWatchDog();
		}
	}

	private void displayProjectWizard() {
		ProjectRegistrationWizardDialogHandler newProjectWizardHandler = new ProjectRegistrationWizardDialogHandler();
		try {
			newProjectWizardHandler.execute(new ExecutionEvent());
		} catch (ExecutionException exception) {
			// when the new project wizard cannot be displayed, new
			// users cannot register with WatchDog.
			WatchDogLogger.getInstance(
					Preferences.getInstance().isLoggingEnabled()).logSevere(
					exception);
		}
	}

	private void savePreferenceStoreIfNeeded() {
		if (preferences.getStore().needsSaving()) {
			try {
				((ScopedPreferenceStore) preferences.getStore()).save();
			} catch (IOException exception) {
				WatchDogLogger.getInstance(
						Preferences.getInstance().isLoggingEnabled())
						.logSevere(exception);
			}
		}
	}
}