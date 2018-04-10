package nl.tudelft.watchdog.core.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.NetworkUtils;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class UserRegistrationStep extends RegistrationStep {
    public UserRegistrationStep(RegistrationWizard wizard) {
        super(wizard);
    }

    @Override
    protected String obtainHeaderText() {
        return "<html>" +
                "<h1>User registration</h1>" +
                "Before we start, we first have to have a WatchDog user registration<br>";
    }

    @Override
    protected Function<Consumer<Boolean>, JPanel> getIdInputPanel() {
        return callback -> new IdInputPanel(callback) {
			private static final long serialVersionUID = -4111382084549265952L;

			@Override
            protected String getIdLabelText() {
                return "Your WatchDog User-ID: ";
            }

            @Override
            protected String getIdTooltipText() {
                return "The User-ID we sent you upon your first WatchDog registration";
            }

            @Override
            protected String createUrlForId(String id) {
                return NetworkUtils.buildExistingUserURL(id);
            }

        };
    }
}
