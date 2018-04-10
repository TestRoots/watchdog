package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.core.ui.wizards.RegistrationWizard;
import org.jetbrains.annotations.Nullable;

public class RegistrationWorkflowWizard extends AbstractWizard<WizardStep> implements RegistrationWizard {
    public RegistrationWorkflowWizard(@Nullable Project project) {
        super("User and Project Registration", project);

        this.addStep(new IntelliJUserWelcomeScreen());
        this.addStep(new IntelliJUserRegistrationStep(this));
        this.addStep(new IntelliJProjectRegistrationStep(this));

        this.init();
    }

    @Nullable
    @Override
    protected String getHelpID() {
        return null;
    }

    @Override
    protected boolean canGoNext() {
        return this.getCurrentStepObject().isFinishedWithStep();
    }

    @Override
    public void updateButtons() {
        super.updateButtons();
    }
}
