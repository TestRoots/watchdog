package nl.tudelft.watchdog.eclipse.ui.handlers;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
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
		savePreferenceStoreIfNeeded(preferences);

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
						&& !projectSetting.enableWatchdog)) {
			return;
		}

		UserRegistrationWizardDialogHandler newUserWizardHandler = new UserRegistrationWizardDialogHandler();
		try {
			int statusCode = (int) newUserWizardHandler
					.execute(new ExecutionEvent());
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

	/**
	 * @return true if a silent user and project registration was successfully.
	 */
	public static boolean makeSilentRegistration() {
		boolean userRegSuccess = true;
		String userId = "";
		Preferences preferences = Preferences.getInstance();
		if (preferences.getUserId() == null
				|| preferences.getUserId().isEmpty()) {
			User user = new User();
			user.programmingExperience = "NA";
			try {
				userId = new JsonTransferer().registerNewUser(user);
			} catch (ServerCommunicationException exception) {
				WatchDogLogger.getInstance().logSevere(exception);
				userRegSuccess = false;
			}

			if (WatchDogUtils.isEmptyOrHasOnlyWhitespaces(userId)) {
				return false;
			}

			preferences.setUserId(userId);
			preferences.registerProjectId(WatchDogUtils.getWorkspaceName(), "");
		}
		savePreferenceStoreIfNeeded(preferences);

		boolean projectRegSucces = registerAnonymousProject(
				preferences.getUserId(), preferences);
		return userRegSuccess && projectRegSucces;
	}

	private static boolean registerAnonymousProject(String userId,
			Preferences preferences) {
		boolean isSuccessfull = true;
		String projectId = "";
		try {
			projectId = new JsonTransferer().registerNewProject(
					new nl.tudelft.watchdog.core.ui.wizards.Project(userId));
		} catch (ServerCommunicationException exception) {
			WatchDogLogger.getInstance().logSevere(exception);
			isSuccessfull = false;
		}

		if (WatchDogUtils.isEmptyOrHasOnlyWhitespaces(projectId)) {
			return false;
		}

		preferences.registerProjectId(WatchDogUtils.getWorkspaceName(),
				projectId);
		preferences.registerProjectUse(WatchDogUtils.getWorkspaceName(), true);
		return isSuccessfull;
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
			savePreferenceStoreIfNeeded(preferences);
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
				registerAnonymousProject(preferences.getUserId(), preferences);
			}
		} catch (ExecutionException exception) {
			// when the new project wizard cannot be displayed, new
			// users cannot register with WatchDog.
			WatchDogLogger.getInstance().logSevere(exception);
		}
	}

	private static void savePreferenceStoreIfNeeded(Preferences preferences) {
		if (preferences.getStore().needsSaving()) {
			try {
				((ScopedPreferenceStore) preferences.getStore()).save();
			} catch (IOException exception) {
				WatchDogLogger.getInstance().logSevere(exception);
			}
		}
	}
}
