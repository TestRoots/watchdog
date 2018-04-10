package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

class ProjectRegistrationStep extends RegistrationStep {
    ProjectRegistrationStep(RegistrationWizard registrationWorkflowWizard) {
        super(registrationWorkflowWizard);
    }

    @Override
    String obtainHeaderText() {
        return "<html>" +
                "<h1>Project registration</h1>" +
                "Now we have to create a new WatchDog project for this workspace<br>";
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getIdInputPanel() {
        return callback -> new IdInputPanel(callback) {
            @Override
            String getIdLabelText() {
                return "The WatchDog project ID: ";
            }

            @Override
            String getIdTooltipText() {
                return "The WatchDog project ID associated with this workspace";
            }

            @Override
            String createUrlForId(String id) {
                return NetworkUtils.buildExistingProjectURL(id);
            }
        };
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
        return ProjectRegistrationInputPanel::new;
    }
}
