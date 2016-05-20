package nl.tudelft.watchdog.intellij.ui.wizards.userregistration;

import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.intellij.ui.wizards.projectregistration.*;

import org.jetbrains.annotations.Nullable;

/**
 * A wizard that allows to register a new user or set an existing user, and then
 * continues with project registration.
 */
public class UserProjectRegistrationWizard extends RegistrationWizardBase {

    /**
     * The first step in the wizard.
     */
    public UserWelcomeStep userWelcomeStep;

    /**
     * The step with all the actual user info (name, email, etc.) on it.
     */
    public UserRegistrationStep userRegistrationStep;

    /**
     * When a user already exists ...
     */
    /* package */ UserIdEnteredEndingStep existingUserEndingStep;

    /**
     * The userid, either entered on this step or as retrieved by the server.
     */
	/* package */ String userid;

    /**
     * Constructor.
     */
    public UserProjectRegistrationWizard(String title, Project project) {
        super(title, project);
        addSteps();
        init();
    }

    @Nullable
    @Override
    protected String getHelpID() {
        return null;
    }

    public void addSteps() {
        userWelcomeStep = new UserWelcomeStep(0, this);
        addStep(userWelcomeStep);
        projectCreatedStep = new ProjectCreatedEndingStep(1, this);
        addStep(projectCreatedStep);
        this.totalSteps = 2;
    }

    @Override
    public int getNextStep(int step) {
        if (this.getCurrentStepObject().canFinish()) {
            return getCurrentStep();
        }
        if (myCurrentStep == userWelcomeStep.getStepId()) {
            return projectCreatedStep.getStepId();
        }
        return super.getNextStep(step);
    }

    @Override
    public int getPreviousStep(int step) {
        if (this.getCurrentStepObject().canFinish()) {
            return -1;
        }
        if (myCurrentStep == projectCreatedStep.getStepId()) {
            // Disable going back.
            return -1;
        }
        return super.getPreviousStep(step);
    }

}
