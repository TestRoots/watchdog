package nl.tudelft.watchdog.core.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProjectRegistrationStep extends RegistrationStep {
    public ProjectRegistrationStep(RegistrationWizard registrationWorkflowWizard) {
        super(registrationWorkflowWizard);
    }

    @Override
    protected String obtainHeaderText() {
        return "<html>" +
                "<h1>Project registration</h1>" +
                "Now we have to create a new WatchDog project for this workspace<br>";
    }

    @Override
    protected Function<Consumer<Boolean>, JPanel> getIdInputPanel() {
        return callback -> new IdInputPanel(callback) {
            @Override
            protected String getIdLabelText() {
                return "The WatchDog project ID: ";
            }

            @Override
            protected String getIdTooltipText() {
                return "The WatchDog project ID associated with this workspace";
            }

            @Override
            protected String createUrlForId(String id) {
                return NetworkUtils.buildExistingProjectURL(id);
            }

        };
    }

    @Override
    protected Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
        return ProjectRegistrationInputPanel::new;
    }
}
