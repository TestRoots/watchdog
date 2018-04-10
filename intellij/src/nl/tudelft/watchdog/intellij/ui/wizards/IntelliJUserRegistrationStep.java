package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.openapi.ui.ComboBox;
import nl.tudelft.watchdog.core.ui.wizards.RegistrationStep;
import nl.tudelft.watchdog.core.ui.wizards.UserRegistrationInputPanel;
import nl.tudelft.watchdog.core.ui.wizards.UserRegistrationStep;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

class IntelliJUserRegistrationStep extends WizardStep {

    private final RegistrationStep delegate;

    IntelliJUserRegistrationStep(RegistrationWorkflowWizard wizard) {
        super();

        this.delegate = new UserRegistrationStep(wizard) {
            @Override
            protected Function<Consumer<Boolean>, JPanel> getRegistrationPanel() {
                return IntellijUserRegistrationInputPanel::new;
            }
        };
    }

    @Override
    void _initWithPanel(Container panel) {
        this.delegate._initWithPanel(panel);
    }

    @Override
    boolean isFinishedWithStep() {
        return this.delegate.isFinishedWithStep();
    }

    private class IntellijUserRegistrationInputPanel extends UserRegistrationInputPanel {
        IntellijUserRegistrationInputPanel(Consumer<Boolean> callback) {
            super(callback);
        }

        @Override
        public JComboBox<String> createComboBox() {
            return new ComboBox<>();
        }
    }
}
