package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static nl.tudelft.watchdog.core.ui.wizards.User.*;

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
                "<h1>" + USER_REGISTRATION_TITLE + "</h1>" +
                BEFORE_USER_REGISTRATION;
    }

    @Override
    String getRegistrationType() {
        return USER;
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getIdInputPanel() {
        return callback -> new IdInputPanel(callback) {
            @Override
            String getIdLabelText() {
                return YOUR_USER_ID_LABEL;
            }

            @Override
            String getIdTooltipText() {
                return USER_ID_TOOLTIP;
            }

            @Override
            String createUrlForId(String id) {
                return NetworkUtils.buildExistingUserURL(id);
            }

            @Override
            void storeIdInPreferences(Preferences preferences, String id) {
                preferences.setUserId(id);
            }
        };
    }

    @Override
    Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
        return UserRegistrationInputPanel::new;
    }
}
