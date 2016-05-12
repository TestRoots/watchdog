package nl.tudelft.watchdog.intellij.ui.wizards.projectregistration;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.intellij.WatchDogStartUp;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationEndingStepBase;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.intellij.ui.wizards.WizardStep;


import javax.swing.*;

/**
 * Possible finishing step in the wizard. If the project exists on the server,
 * or the server is not reachable, the user can exit here.
 */
public class ProjectCreatedEndingStep extends RegistrationEndingStepBase {

    private final String concludingMessage;

    /**
     * Constructor.
     */
    public ProjectCreatedEndingStep(int pageNumber, RegistrationWizardBase wizard) {
        super("Project-ID created.", pageNumber, wizard);
        concludingMessage = ProjectIdEnteredEndingStep.ENCOURAGING_END_MESSAGE;
        setTitle(windowTitle);
    }

    @Override
    protected void makeRegistration() {
        windowTitle = "Registration Summary";
        messageTitle = "";
        if (WatchDogStartUp.makeSilentRegistration()) {
            successfulRegistration = true;
            messageBody = "New user and project successfully registered!";
        } else {
            successfulRegistration = false;
            messageBody = "Registration failed! Do you have an internet connection?";
        }
    }

    private void createPageContent(JPanel parent) {
        setTitle(windowTitle);
        createUserAndProjectRegistrationSummary(parent);
        createDebugSurveyInfo(parent);
        if (successfulRegistration) {
            UIUtils.createLabel(parent, concludingMessage);
        }
    }

    /**
     * Shows the label and link to ask the new user to fill out the survey on
     * debugging.
     */
    private void createDebugSurveyInfo(JPanel parent) {
        UIUtils.createBoldLabel(parent, WatchDogGlobals.DEBUG_SURVEY_TEXT);
        UIUtils.createStartDebugSurveyLink(parent);
    }

    private void createUserAndProjectRegistrationSummary(JPanel parent) {
        if (successfulRegistration) {
            WizardStep.createSuccessMessage(parent, messageTitle, messageBody, null);
            getWizard().getCancelButton().setEnabled(false);
        } else {
            WizardStep.createFailureMessage(parent, messageTitle, messageBody);
        }
    }

    @Override
    protected void commit(CommitType commitType) {
        getWizard().performFinish();
    }

    @Override
    public void _init() {
        super._init();
        makeRegistration();
        JPanel oneColumn = UIUtils.createGridedJPanel(topPanel,1);
        createPageContent(oneColumn);
        topPanel.updateUI();
    }
}
