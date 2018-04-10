package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

class UserRegistrationStep extends RegistrationStep {

    /**
     * The length (in characters) of the WatchDog id.
     */
    static final int ID_LENGTH = 40;

    UserRegistrationStep(RegistrationWorkflowWizard wizard) {
        super(wizard);
    }

    @Override
    String obtainHeaderText() {
        return "<html>" +
                "<h1>User registration</h1>" +
                "Before we start, we first have to have a WatchDog user registration<br>";
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getIdInputPanel() {
        return callback -> new IdInputPanel(callback) {
            @Override
            String getIdLabelText() {
                return "Your WatchDog User-ID: ";
            }

            @Override
            String getIdTooltipText() {
                return "The User-ID we sent you upon your first WatchDog registration";
            }

            @Override
            String createUrlForId(String id) {
                return NetworkUtils.buildExistingUserURL(id);
            }
        };
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
        return UserRegistrationInputPanel::new;
    }
}
