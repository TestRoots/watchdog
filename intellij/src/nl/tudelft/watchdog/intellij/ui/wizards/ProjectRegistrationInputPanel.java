package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.INPUT_IS_OPTIONAL;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

class ProjectRegistrationInputPanel extends RegistrationInputPanel {

    private JTextField projectName;
    private JTextField projectWebsite;
    private ButtonGroup ciUsage;
    private ButtonGroup codeStyleUsage;
    private ButtonGroup bugFindingUsage;
    private ButtonGroup automationUsage;
    private JTextField toolsUsed;

    /**
     * A panel to ask the user questions regarding their project.
     * @param callback The callback invoked after the user clicked "Create WatchDog Project".
     */
    ProjectRegistrationInputPanel(Consumer<Boolean> callback) {
        super(callback);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
                "<h3>" + WATCHDOG_PROJECT_PROFILE + "</h3>" +
                PROJECT_DATA_REQUEST + "<br>" +
                INPUT_IS_OPTIONAL));

		this.inputContainer = new JPanel(new GridLayout(0, 2));
		this.add(inputContainer);

        this.createInputFields();

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));

        this.createButtonAndStatusContainer(CREATE_PROJECT_BUTTON_TEXT);
    }

    private void createInputFields() {
        this.projectName = WizardStep.createLinkedLabelTextField(PROJECT_NAME_LABEL, PROJECT_NAME_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.projectWebsite = WizardStep.createLinkedLabelTextField(PROJECT_WEBSITE_LABEL, PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.ciUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

        inputContainer.add(new JLabel(DO_YOU_USE_STATIC_ANALYSIS));
        inputContainer.add(Box.createGlue());

        this.codeStyleUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(CODE_STYLE_USAGE_LABEL_TEXT, inputContainer);
        this.bugFindingUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(BUG_FINDING_USAGE_LABEL_TEXT, inputContainer);
        this.automationUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(OTHER_AUTOMATION_USAGE_LABEL_TEXT, inputContainer);

        this.toolsUsed = WizardStep.createLinkedLabelTextField(TOOL_USAGE_LABEL_TEXT, TOOL_USAGE_TEXTFIELD_TOOLTIP, 150, inputContainer);
    }

    @Override
    boolean registerAction() {
        Project project = new Project(Preferences.getInstance().getUserId());

        project.name = projectName.getText();
        project.website = projectWebsite.getText();
        project.usesContinuousIntegration = WizardStep.getChoiceFromButtonGroup(ciUsage);
        project.usesCodeStyleSA = WizardStep.getChoiceFromButtonGroup(codeStyleUsage);
        project.usesBugFindingSA = WizardStep.getChoiceFromButtonGroup(bugFindingUsage);
        project.usesOtherAutomationSA = WizardStep.getChoiceFromButtonGroup(automationUsage);
        project.usesToolsSA = toolsUsed.getText();

        String projectId;

        try {
            projectId = new JsonTransferer().registerNewProject(project);
        } catch (Exception exception) {
        	this.createFailureMessage(PROJECT_CREATION_MESSAGE_FAILURE, exception);

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.registerProjectId(project.name, projectId);
        preferences.registerProjectUse(project.name, true);

        this.createSuccessIdOutput(PROJECT_CREATION_MESSAGE_SUCCESSFUL, YOUR_PROJECT_ID_LABEL, projectId);

        return true;
    }
}
