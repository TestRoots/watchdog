package nl.tudelft.watchdog.intellij.ui.wizards.projectregistration;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationEndingStepBase;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.intellij.ui.wizards.WizardStep;
import nl.tudelft.watchdog.intellij.ui.wizards.userregistration.UserRegistrationStep;
import nl.tudelft.watchdog.intellij.ui.wizards.userregistration.UserProjectRegistrationWizard;
import nl.tudelft.watchdog.core.util.WatchDogLogger;


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
        concludingMessage = "<html>You can change these and other WatchDog settings in the IntelliJ Settings.<br>"
                + ProjectIdEnteredEndingStep.ENCOURAGING_END_MESSAGE;
        setTitle(windowTitle);
    }

    @Override
    protected void makeRegistration() {
        Project project = new Project(Preferences.getInstance().getUserId());

        ProjectSliderStep sliderStep;
        ProjectRegistrationStep projectStep = getWizard().projectRegistrationStep;
        if (!getWizard().projectRegistrationStep.shouldSkipProjectSliderStep()) {
            sliderStep = getWizard().projectSliderStep;
            project.productionPercentage = sliderStep.percentageProductionSlider.getValue();
            project.useJunitOnlyForUnitTesting = sliderStep
                    .usesJunitForUnitTestingOnly();
            project.followTestDrivenDesign = sliderStep.usesTestDrivenDesing();
        }

        // initialize from projectPage
        project.belongToASingleSoftware = !projectStep.noSingleProjectCheck.isSelected();
        project.name = projectStep.projectNameInput.getText();
        project.website = projectStep.projectWebsite.getText();
        project.usesContinuousIntegration = projectStep.usesContinuousIntegration();
        project.usesJunit = projectStep.usesJunit();
        project.usesOtherTestingFrameworks = projectStep
                .usesOtherTestingFrameworks();
        project.usesOtherTestingForms = projectStep.usesOtherTestingForms();

        windowTitle = "Registration Summary";

        try {
            id = new JsonTransferer().registerNewProject(project);
        } catch (ServerCommunicationException exception) {
            successfulRegistration = false;
            messageTitle = "Problem creating new project!";
            messageBody = "<html>" + exception.getMessage().replace(". ", ". <br>");
            messageBody += "<br>Are you connected to the internet, and is port 80 open?";
            messageBody += "<br>Please contact us via www.testroots.org. <br>We'll troubleshoot the issue!";
            WatchDogLogger.getInstance().logSevere(exception);
            return;
        }

        successfulRegistration = true;

        getWizard().setProjectId(id);

        messageTitle = "New project registered!";
        messageBody = "Your new project id is registered: ";
    }

    private void createPageContent(JPanel parent) {
        if (isThisProjectWizard()) {
            createProjectRegistrationSummary(parent);
        } else {
            UserProjectRegistrationWizard wizard = (UserProjectRegistrationWizard) getWizard();
            UserRegistrationStep userRegistrationStep = wizard.userRegistrationStep;
            if (wizard.userWelcomeStep.getRegisterNewId()) {
                createDebugSurveyInfo(parent);
                userRegistrationStep.createUserRegistrationSummary(parent);
            }
            createProjectRegistrationSummary(parent);
        }
    }

    /**
     * Shows the label and link to ask the new user to fill out the survey on
     * debugging.
     */
    private void createDebugSurveyInfo(JPanel parent) {
        UIUtils.createBoldLabel(parent, "Do you ever debug? Did you know WatchDog now also reports on debugging?");
        UIUtils.createStartDebugSurveyLink(parent);
    }

    private void createProjectRegistrationSummary(JPanel parent) {
        if (successfulRegistration) {
            WizardStep.createSuccessMessage(parent, messageTitle, messageBody, id);
            UIUtils.createLabel(parent, concludingMessage);
            getWizard().getCancelButton().setEnabled(false);
        } else {
            WizardStep.createFailureMessage(parent, messageTitle, messageBody);
        }
    }

    private boolean isThisProjectWizard() {
        RegistrationWizardBase wizard = getWizard();
        return wizard instanceof ProjectRegistrationWizard;
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
