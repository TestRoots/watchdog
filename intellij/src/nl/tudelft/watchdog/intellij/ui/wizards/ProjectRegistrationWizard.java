package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class ProjectRegistrationWizard extends AbstractWizard<WizardStep> implements RegistrationWizard {
    public ProjectRegistrationWizard(Project project) {
        super("Project Registration", project);

        this.addStep(new ProjectRegistrationStep(this));

        this.init();
    }

    @Nullable
    @Override
    protected String getHelpID() {
        return null;
    }

    @Override
    public void updateButtons() {
        super.updateButtons();
    }
}
