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

	/** The first step in the wizard. */
	public UserWelcomeStep userWelcomeStep;

	/** The step with all the actual user info (name, email, etc.) on it. */
	public UserRegistrationStep userRegistrationStep;

	/** When a user already exists ... */
	/* package */UserIdEnteredEndingStep existingUserEndingStep;

	/**
	 * The userid, either entered on this step or as retrieved by the server.
	 */
	/* package */String userid;

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
//		userRegistrationStep = new UserRegistrationStep(1, this);
//		addStep(userRegistrationStep);
//		existingUserEndingStep = new UserIdEnteredEndingStep(2, this);
//		addStep(existingUserEndingStep);
//		existingProjectIdStep = new ProjectIdEnteredEndingStep(3, this);
//		addStep(existingProjectIdStep);
//		projectWelcomeStep = new ProjectWelcomeStep(4, this);
//		addStep(projectWelcomeStep);
//		projectRegistrationStep = new ProjectRegistrationStep(5, this);
//		addStep(projectRegistrationStep);
//		projectSliderStep = new ProjectSliderStep(6, this);
//		addStep(projectSliderStep);
//		projectedCreatedStep = new ProjectCreatedEndingStep(7, this);
//		addStep(projectedCreatedStep);
		this.totalSteps = 1;
	}

    @Override
	public int getNextStep(int step) {
        if(this.getCurrentStepObject().canFinish()) return getCurrentStep();
//		if (myCurrentStep == userWelcomeStep.getStepId()
//				&& !userWelcomeStep.getRegisterNewId()) {
//			return existingUserEndingStep.getStepId();
//		}
//		if (myCurrentStep == existingUserEndingStep.getStepId()) {
//			return projectWelcomeStep.getStepId();
//		}
//		if (myCurrentStep == userRegistrationStep.getStepId()) {
//			return projectRegistrationStep.getStepId();
//		}
//		if (myCurrentStep == projectWelcomeStep.getStepId()
//				&& !projectWelcomeStep.getRegisterNewId()) {
//			return existingProjectIdStep.getStepId();
//		}
//		if (myCurrentStep == projectRegistrationStep.getStepId()
//				&& projectRegistrationStep.shouldSkipProjectSliderStep()) {
//			return projectedCreatedStep.getStepId();
//		}
//		if (myCurrentStep == projectSliderStep.getStepId()) {
//			return projectedCreatedStep.getStepId();
//		}
		return super.getNextStep(step);
	}

    @Override
	public int getPreviousStep(int step) {
        if(this.getCurrentStepObject().canFinish()) return -1;
//		if (myCurrentStep == existingUserEndingStep.getStepId()
//				&& !userWelcomeStep.getRegisterNewId()) {
//			return userWelcomeStep.getStepId();
//		}
//		if (myCurrentStep == projectWelcomeStep.getStepId()) {
//			return existingUserEndingStep.getStepId();
//		}
//		if (myCurrentStep == existingProjectIdStep.getStepId()
//				&& !projectWelcomeStep.getRegisterNewId()) {
//			return projectWelcomeStep.getStepId();
//		}
//		if (myCurrentStep == projectRegistrationStep.getStepId()) {
//			// Disable going back if a new user id is being created.
//			return userWelcomeStep.getRegisterNewId() ? -1
//					: projectWelcomeStep.getStepId();
//		}
//        if(myCurrentStep == projectedCreatedStep.getStepId() && projectRegistrationStep.shouldSkipProjectSliderStep()) {
//            return projectRegistrationStep.getStepId();
//        }
		return super.getPreviousStep(step);
	}

}
