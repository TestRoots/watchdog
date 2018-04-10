package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.ui.wizards.ProjectRegistrationStep;
import nl.tudelft.watchdog.core.ui.wizards.RegistrationWizard;

import java.awt.*;

class IntelliJProjectRegistrationStep extends WizardStep {
    private final ProjectRegistrationStep delegate;

    IntelliJProjectRegistrationStep(RegistrationWizard wizard) {
        super();

        this.delegate = new ProjectRegistrationStep(wizard);
    }

    @Override
    void _initWithPanel(Container panel) {
        this.delegate._initWithPanel(panel);
    }

    @Override
    boolean isFinishedWithStep() {
        return this.delegate.isFinishedWithStep();
    }
}
