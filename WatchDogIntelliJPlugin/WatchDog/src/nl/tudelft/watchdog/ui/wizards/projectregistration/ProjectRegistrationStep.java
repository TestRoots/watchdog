package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.ui.wizards.WizardStep;
import nl.tudelft.watchdog.core.ui.wizards.YesNoDontKnowChoice;
import nl.tudelft.watchdog.util.WatchDogUtils;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A step that allows the user to enter how much time he spend on testing vs.
 * production code.
 */
public class ProjectRegistrationStep extends WizardStep {

    private static final String TITLE = "Register a new project";

    private static final String DOES_YOUR_PROJECT = "Does your project use ...";

    private static final String DOES_AT_LEAST_ONE_PROJECT_USE = "Does at least one of your projects use ...";

    /**
     * Project name.
     */
    JTextField projectNameInput;

    /**
     * Project website
     */
    JTextField projectWebsite;

    private JLabel multipleProjectLabel;

    private JPanel noSingleProjectPanel;

    private JPanel useContinuousIntegration;

    private JPanel useJunit;

    private JPanel otherTestingFrameworks;

    private JPanel otherTestingForms;

    /**
     * No, these projects do not belong to a single physical project.
     */
    JRadioButton noSingleProjectCheck;

    /**
     * Yes, these projects belong to a single physical project.
     */
    JRadioButton yesSingleProjectButton;

    /**
     * Constructor.
     */
    public ProjectRegistrationStep(int pageNumber, RegistrationWizardBase wizard) {
        super("Register Project", pageNumber, wizard);
        setTitle(TITLE);
        descriptionText = "Create a new WatchDog Project for this workspace!";
    }


    private void createMainPanel(final JPanel parent) {
        JPanel singleProjectPanel = UIUtils.createFlowJPanelLeft(parent);
        UIUtils.createLabel(singleProjectPanel,
                "Do all modules in this project belong to one 'larger' project? ");
        noSingleProjectPanel = createSimpleYesNoQuestion(singleProjectPanel);
        noSingleProjectCheck = (JRadioButton) noSingleProjectPanel.getComponent(1);
        yesSingleProjectButton = (JRadioButton) noSingleProjectPanel.getComponent(0);

        noSingleProjectCheck
                .addChangeListener(new SingleProjectSelectionListener(
                        parent, noSingleProjectCheck));
        yesSingleProjectButton
                .addChangeListener(new SingleProjectSelectionListener(
                        parent, noSingleProjectCheck));

        JPanel projectNamePanel = UIUtils.createFlowJPanelLeft(parent);
        projectNameInput = UIUtils.createLinkedFieldInput(projectNamePanel, "Project Name:      ",
                20, "The name of the project(s) you work on in this workspace");
        JPanel projectWebsitePanel = UIUtils.createFlowJPanelLeft(parent);
        projectWebsite = UIUtils.createLinkedFieldInput(projectWebsitePanel, "Project Website:  ",
                20, "If you have a website, we'd love to see it here.");

        FormValidationListener formValidationListener = new FormValidationListener(
                this);
        projectNameInput.getDocument().addDocumentListener(formValidationListener);
        projectWebsite.getDocument().addDocumentListener(formValidationListener);

        JPanel CIQuestionPanel = UIUtils.createFlowJPanelLeft(parent);
        UIUtils.createLabel(CIQuestionPanel, "Does your project use any Continuous Integration tools (Travis, Jenkins, etc.)?");
        JPanel answerPanel = UIUtils.createFlowJPanelCenter(CIQuestionPanel);
        useContinuousIntegration = createSimpleYesNoDontKnowQuestion(answerPanel);
        addValidationListenerToAllChildren(useContinuousIntegration, formValidationListener);

        JPanel mainQuestionPanel = UIUtils.createGridedJPanel(parent, 1);
        UIUtils.createLabel(mainQuestionPanel, "");
        multipleProjectLabel = UIUtils.createLabel(mainQuestionPanel,
                DOES_YOUR_PROJECT);

        JPanel questionsPanel = UIUtils.createGridedJPanel(
                parent, 2);

        UIUtils.createLabel(questionsPanel, "  ... unit testing framework (e.g. JUnit)? ");
        JPanel answerPanel1 = UIUtils.createFlowJPanelCenter(questionsPanel);
        UIUtils.createLabel(questionsPanel, "  ... other testing frameworks (e.g. Mockito)? ");
        JPanel answerPanel2 = UIUtils.createFlowJPanelCenter(questionsPanel);
        UIUtils.createLabel(questionsPanel, "  ... other testing forms (e.g. manual testing)? ");
        JPanel answerPanel3 = UIUtils.createFlowJPanelCenter(questionsPanel);

        useJunit = createSimpleYesNoDontKnowQuestion(answerPanel1);
        otherTestingFrameworks = createSimpleYesNoDontKnowQuestion(answerPanel2);
        otherTestingForms = createSimpleYesNoDontKnowQuestion(answerPanel3);

        addValidationListenerToAllChildren(useJunit, formValidationListener);
        addValidationListenerToAllChildren(otherTestingFrameworks, formValidationListener);
        addValidationListenerToAllChildren(otherTestingForms, formValidationListener);

    }

    @Override
    public void validateFormInputs() {
        if (!hasOneSelection(noSingleProjectPanel)) {
            setErrorMessageAndStepComplete("Please answer all yes/no questions!");
        } else if (inputFieldDoesNotHaveMinimumSensibleInput(projectNameInput)
                && projectNameInput.isEnabled()) {
            setErrorMessageAndStepComplete("You must enter a project name longer than 2 chars.");
        } else if (!hasOneSelection(useContinuousIntegration)
                || !hasOneSelection(useJunit)
                || !hasOneSelection(otherTestingFrameworks)
                || !hasOneSelection(otherTestingForms)) {
            setErrorMessageAndStepComplete("Please answer all yes/no/don't know questions!");
        } else {
            setErrorMessageAndStepComplete(null);
        }
        updateStep();
    }

    private boolean inputFieldDoesNotHaveMinimumSensibleInput(JTextField input) {
        return WatchDogUtils.isEmptyOrHasOnlyWhitespaces(input.getText())
                || input.getText().length() < 3;
    }


    @Override
    protected void commit(CommitType commitType) {

    }

    @Override
    public boolean canFinish() {
        return false;
    }


    /**
     * @return Whether or not this project uses Continuous Integration tools.
     */
    /* package */YesNoDontKnowChoice usesContinuousIntegration() {
        return evaluateWhichSelection(useContinuousIntegration);
    }

    /**
     * @return Whether or not this project uses Junit.
     */
	/* package */YesNoDontKnowChoice usesJunit() {
        return evaluateWhichSelection(useJunit);
    }

    /**
     * @return Whether or not this project uses other testing frameworks (than
     * Junit).
     */
	/* package */YesNoDontKnowChoice usesOtherTestingFrameworks() {
        return evaluateWhichSelection(otherTestingFrameworks);
    }

    /**
     * @return Whether or not this project uses other testing strategies.
     */
	/* package */YesNoDontKnowChoice usesOtherTestingForms() {
        return evaluateWhichSelection(otherTestingForms);
    }

    /**
     * @return Whether the {@link ProjectSliderStep}, which logically follows
     * this page in the wizard, can be skipped (because the selection by
     * the user in this wizard don't make sense to fill-out the next
     * page).
     */
    public boolean shouldSkipProjectSliderStep() {
        return usesOtherTestingFrameworks() == YesNoDontKnowChoice.No
                && usesJunit() == YesNoDontKnowChoice.No;
    }

    @Override
    public void _init() {
        super._init();
        JPanel oneColumn = UIUtils.createVerticalBoxJPanel(topPanel);
        createHeader(oneColumn);
        createMainPanel(oneColumn);
        setComplete(false);
    }

    private class SingleProjectSelectionListener implements ChangeListener {
        private final JRadioButton noSingleProjectButton;
        private final JPanel parent;

        private SingleProjectSelectionListener(JPanel parent, JRadioButton noSingleProjectButton) {
            this.noSingleProjectButton = noSingleProjectButton;
            this.parent = parent;
        }

        private void performUIUpdate(String label, boolean enableState) {
            multipleProjectLabel.setText(label);
            multipleProjectLabel.updateUI();
            projectNameInput.setEnabled(enableState);
            projectWebsite.setEnabled(enableState);
            parent.updateUI();
            validateFormInputs();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (noSingleProjectButton.isSelected()) {
                yesSingleProjectButton.setSelected(false);
                performUIUpdate(DOES_AT_LEAST_ONE_PROJECT_USE, false);
            } else if (yesSingleProjectButton.isSelected()) {
                noSingleProjectButton.setSelected(false);
                performUIUpdate(DOES_YOUR_PROJECT, true);
            }
        }
    }

}
