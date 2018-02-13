package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.ide.wizard.CommitStepCancelledException;
import com.intellij.ide.wizard.CommitStepException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.IdeFocusManager;

import javax.swing.*;

import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.ui.wizards.projectregistration.*;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;


/**
 * Base class for User and Project registration wizards.
 */
public abstract class RegistrationWizardBase extends AbstractWizard<WizardStep> {
    /**
     * The project-id, either entered on the previous wizard pages or as
     * retrieved by the server.
     */
    protected String projectId;

    /**
     * Maximum number of pages in the current wizard.
     */
    protected int totalSteps;

    /**
     * Step with Yes/No question about registering new project ID.
     */
    public ProjectWelcomeStep projectWelcomeStep;

    /**
     * Step after existing project ID has been entered.
     */
    protected ProjectIdEnteredEndingStep existingProjectIdStep;

    /**
     * First page of project registration.
     */
    public ProjectRegistrationStep projectRegistrationStep;

    /**
     * Second part of project registration.
     */
    public ProjectSliderStep projectSliderStep;

    /**
     * Project registration completed.
     */
    protected ProjectCreatedEndingStep projectedCreatedStep;

    /**
     * Constructor.
     */
    public RegistrationWizardBase(String title, com.intellij.openapi.project.Project project) {
        super(title, project);
    }

    /**
     * Overridden because we want to call _commitPrev() on pressed "Previous" button.
     */
    @Override
    protected void doPreviousAction() {
        // Commit data of current step
        final WizardStep currentStep = mySteps.get(myCurrentStep);
        try {
            currentStep._commitPrev();
        } catch (final CommitStepCancelledException e) {
            return;
        } catch (CommitStepException e) {
            Messages.showErrorDialog(myContentPanel, e.getMessage());
            return;
        }

        myCurrentStep = getPreviousStep(myCurrentStep);
        updateStep();
    }

    @Override
    protected void updateStep() {
        super.updateStep();
        updateButtons();
        final WizardStep step = getCurrentStepObject();
        final JComponent toFocus = step.getPreferredFocusedComponent();
        if (toFocus != null) {
            IdeFocusManager.findInstanceByComponent(getWindow()).requestFocus(toFocus, true);
        }
    }

    @Override
    protected void updateButtons() {
        super.updateButtons();
        getPreviousButton().setEnabled(getPreviousStep(myCurrentStep) >= 0);
        getNextButton().setEnabled(getCurrentStepObject().isComplete() && !isLastStep() || isLastStep() && canFinish());
    }

    @Override
    protected boolean canGoNext() {
        return getCurrentStepObject().isComplete();
    }

    public void performFinish() {
        Preferences preferences = Preferences.getInstance();
        preferences.registerProjectId(WatchDogUtils.getProjectName(), projectId);
        preferences.registerProjectUse(WatchDogUtils.getProjectName(), true);
    }

    @Override
    protected int getPreviousStep(int step) {
        return --step;
    }

    @Override
    public boolean canFinish() {
        return this.getCurrentStepObject().canFinish();
    }

    /**
     * Returns project-ID.
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Sets projectId.
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * Returns total number of pages in current wizard.
     */
    public int getTotalSteps() {
        return totalSteps;
    }

    /**
     * (Sets total number of pages in current wizard.
     */
    public void setTotalStepNumber(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    @Override
    protected void doHelpAction() {
        BrowserUtil.open("http://testroots.org/testroots_watchdog.html");
    }
}
