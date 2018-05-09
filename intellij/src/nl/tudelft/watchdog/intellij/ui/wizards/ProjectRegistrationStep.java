package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;

class ProjectRegistrationStep extends RegistrationStep {
    ProjectRegistrationStep(RegistrationWizard registrationWorkflowWizard) {
        super(registrationWorkflowWizard);
    }

    @Override
    String obtainHeaderText() {
        return "<html>" +
                "<h1>" + PROJECT_REGISTRATION_TITLE + "</h1>" +
                BEFORE_PROJECT_REGISTRATION;
    }

    @Override
    String getRegistrationType() {
        return PROJECT;
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getIdInputPanel() {
        return callback -> new IdInputPanel(callback) {
            @Override
            String getIdLabelText() {
                return PROJECT_ID_LABEL;
            }

            @Override
            String getIdTooltipText() {
                return PROJECT_ID_TOOLTIP;
            }

            @Override
            String createUrlForId(String id) {
                return NetworkUtils.buildExistingProjectURL(id);
            }

            @Override
            void storeIdInPreferences(Preferences preferences, String id) {
                preferences.registerProjectId(WatchDogUtils.getProjectName(), id);
                preferences.registerProjectUse(WatchDogUtils.getProjectName(), true);
            }
        };
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
        return ProjectRegistrationInputPanel::new;
    }
}
