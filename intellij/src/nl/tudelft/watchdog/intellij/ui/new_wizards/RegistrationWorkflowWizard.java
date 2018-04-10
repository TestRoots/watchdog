package nl.tudelft.watchdog.intellij.ui.new_wizards;

import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class RegistrationWorkflowWizard extends AbstractWizard<WizardStep> {
    public RegistrationWorkflowWizard(String title, @Nullable Project project) {
        super(title, project);

        this.addStep(new UserWelcomeScreen());
        this.addStep(new UserRegistrationStep(this));
        this.addStep(new ProjectRegistrationStep());

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
