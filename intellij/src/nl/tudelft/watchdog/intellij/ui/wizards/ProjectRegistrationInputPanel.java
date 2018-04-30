package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.intellij.ui.wizards.UserRegistrationStep.ID_LENGTH;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

class ProjectRegistrationInputPanel extends JPanel {

    private final Container buttonContainer;
    private JTextField projectName;
    private JTextField projectWebsite;
    private ButtonGroup ciUsage;
    private ButtonGroup codeStyleUsage;
    private ButtonGroup bugFindingUsage;
    private ButtonGroup automationUsage;
    private JTextField toolsUsed;
    private Container statusContainer;

    /**
     * A panel to ask the user questions regarding their project.
     * @param callback The callback invoked after the user clicked "Create WatchDog Project".
     */
    ProjectRegistrationInputPanel(Consumer<Boolean> callback) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
                "<h3>" + WATCHDOG_PROJECT_PROFILE + "</h3>" +
                PROJECT_DATA_REQUEST + "<br>" +
                INPUT_IS_OPTIONAL));

        this.createInputFields();

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));
        this.buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(buttonContainer);

        this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(statusContainer);

        JButton button = new JButton(CREATE_PROJECT_BUTTON_TEXT);
        this.buttonContainer.add(button);
        button.addActionListener(actionEvent -> {
            this.statusContainer.removeAll();

            this.buttonContainer.removeAll();
            this.buttonContainer.add(button);

            callback.accept(registerProject());
        });
    }

    private void createInputFields() {
        JPanel inputContainer = new JPanel(new GridLayout(0, 2));
        this.add(inputContainer);

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

    private boolean registerProject() {
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
        } catch (ServerCommunicationException exception) {
            buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
            buttonContainer.add(new JLabel(PROJECT_CREATION_MESSAGE_FAILURE));

            statusContainer.add(WizardStep.createErrorMessageLabel(exception));

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.registerProjectId(project.name, projectId);
        preferences.registerProjectUse(project.name, true);

        buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
        buttonContainer.add(new JLabel(PROJECT_CREATION_MESSAGE_SUCCESSFUL));

        statusContainer.add(new JLabel(YOUR_PROJECT_ID_LABEL));

        JTextField userIdField = new JTextField(projectId, ID_LENGTH);
        userIdField.setEditable(false);
        statusContainer.add(userIdField);

        return true;
    }
}
