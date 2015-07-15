package nl.tudelft.watchdog;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.ViewToolWindowButtonsAction;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import nl.tudelft.watchdog.logic.InitializationManager;
import nl.tudelft.watchdog.core.logic.ui.events.WatchDogEvent;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.core.ui.preferences.ProjectPreferenceSetting;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectRegistrationWizard;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserProjectRegistrationWizard;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.core.util.WatchDogLogger;
import nl.tudelft.watchdog.util.WatchDogUtils;
import org.jetbrains.annotations.NotNull;

public class WatchDog implements ProjectComponent {
    public static Project project;

    /** The warning displayed when WatchDog is not active. */
    public static final String WATCHDOG_INACTIVE_WARNING = "<html>WatchDog only works when you register a (possibly anonymous) user and project.<br><br>Takes less than one minute,  and you can win prices. As a registered user, you decide where WatchDog is active.";

    /** Whether the user has cancelled the user project registration wizard. */
    private boolean userProjectRegistrationCancelled = false;


    public WatchDog(Project project) {
        WatchDog.project = project;
    }

    public void initComponent() {
        WatchDogGlobals.logDirectory = PluginManager.getPlugin(PluginId.findId("nl.tudelft.watchdog")).getPath().toString() + "/logs/";
        WatchDogGlobals.preferences = Preferences.getInstance();
    }

    public void disposeComponent() {
        // component disposal logic if needed
    }

    @NotNull
    public String getComponentName() {
        return "WatchDog";
    }

    public void projectOpened() {
        // called when project is opened

        checkWhetherToDisplayUserProjectRegistrationWizard();

        if (WatchDogUtils.isEmpty(WatchDogGlobals.preferences.getUserid())
                || userProjectRegistrationCancelled) {
            return;
        }
        checkIsProjectAlreadyRegistered();
        checkWhetherToDisplayProjectWizard();
        checkWhetherToStartWatchDog();
    }
    public void projectClosed() {
        // called when project is being closed
        InitializationManager intervalInitializationManager = InitializationManager
                .getInstance();
        intervalInitializationManager.getEventManager().update(new WatchDogEvent(this, WatchDogEvent.EventType.END_IDE));
        intervalInitializationManager.getIntervalManager().closeAllIntervals();
        intervalInitializationManager.getTransferManager().sendIntervalsImmediately();
        intervalInitializationManager.shutdown();
    }

    /** Checks whether there is a registered WatchDog user */
    private void checkWhetherToDisplayUserProjectRegistrationWizard() {
        if (!WatchDogUtils.isEmpty(WatchDogGlobals.preferences.getUserid()))
            return;
        UserProjectRegistrationWizard wizard = new UserProjectRegistrationWizard("User and Project Registration", project);
        wizard.setCrossClosesWindow(false);
        wizard.show();
        if (wizard.getExitCode() == DialogWrapper.CANCEL_EXIT_CODE) {
            Messages.showWarningDialog(WATCHDOG_INACTIVE_WARNING, "WatchDog not active!");
                userProjectRegistrationCancelled = true;
            }
    }

    private void checkIsProjectAlreadyRegistered() {
        if (!WatchDogGlobals.preferences.isProjectRegistered(project.getName())) {
            boolean useWatchDogInThisWorkspace = Messages.YES ==
                    Messages.showYesNoDialog("Should WatchDog be active in this workspace?", "WatchDog Workspace Registration", AllIcons.General.QuestionDialog);
            WatchDogLogger.getInstance(Preferences.getInstance().isLoggingEnabled()).logInfo("Registering workspace...");
            WatchDogGlobals.preferences.registerProjectUse(project.getName(), useWatchDogInThisWorkspace);
       }
    }

    private void checkWhetherToDisplayProjectWizard() {
        ProjectPreferenceSetting setting = WatchDogGlobals.preferences
                .getOrCreateProjectSetting(project.getName());
        if (setting.enableWatchdog && WatchDogUtils.isEmpty(setting.projectId)) {
            new ProjectRegistrationWizard("Project Registration", project).show();
        }
    }

    private void checkWhetherToStartWatchDog() {
        // reload setting from preferences
        ProjectPreferenceSetting setting = WatchDogGlobals.preferences
                .getOrCreateProjectSetting(project.getName());
        if (setting.enableWatchdog) {
            WatchDogLogger.getInstance(Preferences.getInstance().isLoggingEnabled()).logInfo("Starting WatchDog ...");
            WatchDogGlobals.hostIDE = WatchDogGlobals.IDE.INTELLIJ;
            InitializationManager.getInstance();
            WatchDogGlobals.isActive = true;
            new ViewToolWindowButtonsAction().setSelected(null, true);
        }
    }

}
