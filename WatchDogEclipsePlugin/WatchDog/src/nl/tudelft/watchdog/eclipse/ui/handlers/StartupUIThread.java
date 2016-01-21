package nl.tudelft.watchdog.eclipse.ui.handlers;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.logic.network.WatchDogTransferable;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/**
 * The UI Thread in which all the registration process on startup of WatchDog is
 * executed.
 */
public class StartupUIThread implements Runnable {

	/** The warning displayed when WatchDog is not active. */
	public static final String WATCHDOG_INACTIVE_WARNING = "Warning: You can only use WatchDog when you register it.\n\nLast chance: Register now, anonymously, without filling the survey?";

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

		if (WatchDogUtils.isEmpty(preferences.getUserId())
				|| userProjectRegistrationCancelled) {
			return;
		}

		checkIsWorkspaceAlreadyRegistered();
		checkWhetherToDisplayProjectWizard();
		checkWhetherToStartWatchDog();
	}

	/** Checks whether there is a registered WatchDog user */
	private void checkWhetherToDisplayUserProjectRegistrationWizard() {
		ProjectPreferenceSetting projectSetting = preferences
				.getOrCreateProjectSetting(workspaceName);
		if (!WatchDogUtils.isEmpty(preferences.getUserId())
				|| (projectSetting.startupQuestionAsked
						&& !projectSetting.enableWatchdog))
			return;

		UserRegistrationWizardDialogHandler newUserWizardHandler = new UserRegistrationWizardDialogHandler();
		try {
			int statusCode = (int) newUserWizardHandler
					.execute(new ExecutionEvent());
			savePreferenceStoreIfNeeded();
			if (statusCode == Window.CANCEL) {
				boolean shouldRegisterAnonymously = MessageDialog.openQuestion(
						null, "WatchDog not active!",
						WATCHDOG_INACTIVE_WARNING);
				if (shouldRegisterAnonymously) {
					makeSilentRegistration();
				} else {
					userProjectRegistrationCancelled = true;
					preferences.registerProjectUse(
							WatchDogUtils.getWorkspaceName(), false);
				}
			}
		} catch (ExecutionException exception) {
			// when the new user wizard cannot be displayed, new
			// users cannot register with WatchDog.
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}

	private void makeSilentRegistration() {
		String userId = "";
		if (preferences.getUserId() == null
				|| preferences.getUserId().isEmpty()) {
			User user = new User();
			user.programmingExperience = "NA";
			try {
				userId = new JsonTransferer<WatchDogTransferable>()
						.registerNewUser(user);
			} catch (ServerCommunicationException exception) {
				WatchDogLogger.getInstance().logSevere(exception);
			}

			if (WatchDogUtils.isEmptyOrHasOnlyWhitespaces(userId)) {
				return;
			}

			preferences.setUserId(userId);
			preferences.registerProjectId(WatchDogUtils.getWorkspaceName(), "");
		}
		savePreferenceStoreIfNeeded();

		registerAnonymousProject(preferences.getUserId());
	}

	private void registerAnonymousProject(String userId) {
		String projectId = "";
		try {
			projectId = new JsonTransferer<WatchDogTransferable>()
					.registerNewProject(
							new nl.tudelft.watchdog.core.ui.wizards.Project(
									userId));
		} catch (ServerCommunicationException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
		}

		if (WatchDogUtils.isEmptyOrHasOnlyWhitespaces(projectId)) {
			return;
		}

		preferences.registerProjectId(WatchDogUtils.getWorkspaceName(),
				projectId);
		preferences.registerProjectUse(WatchDogUtils.getWorkspaceName(), true);
	}

	private void checkIsWorkspaceAlreadyRegistered() {
		if (!preferences.isProjectRegistered(workspaceName)) {
			boolean useWatchDogInThisWorkspace = MessageDialog.openQuestion(
					null, "WatchDog Workspace Registration",
					"Should WatchDog be active in this workspace?");
			WatchDogLogger.getInstance().logInfo("Registering workspace...");
			preferences.registerProjectUse(workspaceName,
					useWatchDogInThisWorkspace);
		}
	}

	private void checkWhetherToDisplayProjectWizard() {
		ProjectPreferenceSetting setting = preferences
				.getOrCreateProjectSetting(workspaceName);
		if (setting.enableWatchdog
				&& WatchDogUtils.isEmpty(setting.projectId)) {
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
			int statusCode = (int) newProjectWizardHandler
					.execute(new ExecutionEvent());
			if (statusCode == Window.CANCEL) {
				registerAnonymousProject(preferences.getUserId());
			}
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
