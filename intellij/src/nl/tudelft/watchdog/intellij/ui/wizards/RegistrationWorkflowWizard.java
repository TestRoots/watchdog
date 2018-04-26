package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class RegistrationWorkflowWizard extends AbstractWizard<WizardStep> implements RegistrationWizard {
    public RegistrationWorkflowWizard(@Nullable Project project) {
        super("User and Project Registration", project);

        this.addStep(new UserWelcomeScreen());
        this.addStep(new UserRegistrationStep(this));
        this.addStep(new ProjectRegistrationStep(this));

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

    @Override
    protected void doHelpAction() {
        BrowserUtil.open("http://testroots.org/testroots_watchdog.html");
    }
}
