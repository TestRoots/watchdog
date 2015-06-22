package nl.tudelft.watchdog.ui.wizards.projectregistration;

import com.intellij.openapi.project.Project;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;
import org.jetbrains.annotations.Nullable;

/** Wizard showing the project registration for WatchDog. */
public class ProjectRegistrationWizard extends RegistrationWizardBase {

    /**
     * Constructor.
     */
    public ProjectRegistrationWizard(String title, Project project) {
        super(title, project);
        addSteps();
        init();
    }

	public void addSteps() {
		projectWelcomeStep = new ProjectWelcomeStep(0, this);
		addStep(projectWelcomeStep);
		projectRegistrationStep = new ProjectRegistrationStep(1, this);
		addStep(projectRegistrationStep);
		projectSliderStep = new ProjectSliderStep(2, this);
		addStep(projectSliderStep);
		existingProjectIdStep = new ProjectIdEnteredEndingStep(3, this);
		addStep(existingProjectIdStep);
		projectedCreatedStep = new ProjectCreatedEndingStep(4, this);
		addStep(projectedCreatedStep);
		this.totalSteps = 4;
	}

	@Override
	public int getNextStep(int page) {
        if(this.getCurrentStepObject().canFinish()) return getCurrentStep();
		if (myCurrentStep == projectWelcomeStep.getStepId()
				&& !projectWelcomeStep.getRegisterNewId()) {
			return existingProjectIdStep.getStepId();
		}
		if (myCurrentStep == projectRegistrationStep.getStepId()
				&& projectRegistrationStep.shouldSkipProjectSliderStep()) {
			return projectedCreatedStep.getStepId();
		}
		if (myCurrentStep == projectSliderStep.getStepId()) {
			return projectedCreatedStep.getStepId();
		}
		return super.getNextStep(page);
	}

    @Override
    public int getPreviousStep(int step) {
        if(this.getCurrentStepObject().canFinish()) return -1;
        if (myCurrentStep == existingProjectIdStep.getStepId()) {
            return projectWelcomeStep.getStepId();
        }
        if(myCurrentStep == projectedCreatedStep.getStepId() && projectRegistrationStep.shouldSkipProjectSliderStep()) {
            return projectRegistrationStep.getStepId();
        }
        return super.getPreviousStep(step);
    }

    @Nullable
    @Override
    protected String getHelpID() {
        return null;
    }
}
