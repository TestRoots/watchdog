package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;
import static nl.tudelft.watchdog.intellij.ui.wizards.UserRegistrationStep.ID_LENGTH;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

class ProjectRegistrationInputPanel extends JPanel {

    private final JTextField projectName;
    private final JTextField projectWebsite;
    private final ButtonGroup ciUsage;
    private final ButtonGroup codeStyleUsage;
    private final ButtonGroup bugFindingUsage;
    private final ButtonGroup automationUsage;
    private final Container buttonContainer;
    private final JTextField toolsUsed;
    private Container statusContainer;

    ProjectRegistrationInputPanel(Consumer<Boolean> callback) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel inputContainer = new JPanel(new GridLayout(0, 2));
        this.add(inputContainer);

        this.projectName = WizardStep.createLinkedLabelTextField("Project name: ", PROJECT_NAME_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.projectWebsite = WizardStep.createLinkedLabelTextField("Project website: ", PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.ciUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

        inputContainer.add(new JLabel("Does your project use static analysis tools to..."));
        inputContainer.add(Box.createGlue());

        this.codeStyleUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(CODE_STYLE_USAGE_LABEL_TEXT, inputContainer);
        this.bugFindingUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(BUG_FINDING_USAGE_LABEL_TEXT, inputContainer);
        this.automationUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(OTHER_AUTOMATION_USAGE_LABEL_TEXT, inputContainer);

        this.toolsUsed = WizardStep.createLinkedLabelTextField(TOOL_USAGE_LABEL_TEXT, TOOL_USAGE_TEXTFIELD_TOOLTIP, 150, inputContainer);

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));
        this.buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(buttonContainer);

        this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(statusContainer);

        JButton button = new JButton("Create new WatchDog project");
        this.buttonContainer.add(button);
        button.addActionListener(actionEvent -> {
            this.statusContainer.removeAll();

            this.buttonContainer.removeAll();
            this.buttonContainer.add(button);

            callback.accept(registerProject());
        });
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

        statusContainer.add(new JLabel("Your Project ID is: "));

        JTextField userIdField = new JTextField(projectId, ID_LENGTH);
        userIdField.setEditable(false);
        statusContainer.add(userIdField);

        return true;
    }
}
