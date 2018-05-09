package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_REGISTRATION_TITLE;

/**
 * Wizard that asks users for registering a newly imported project with WatchDog.
 */
public class ProjectRegistrationWizard extends AbstractWizard<WizardStep> implements RegistrationWizard {
    public ProjectRegistrationWizard(Project project) {
        super(PROJECT_REGISTRATION_TITLE, project);

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
