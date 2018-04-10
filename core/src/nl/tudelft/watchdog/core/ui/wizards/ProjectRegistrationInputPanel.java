package nl.tudelft.watchdog.core.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.preferences.PreferencesBase;
import nl.tudelft.watchdog.core.util.WatchDogGlobals;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.WizardUIElements.DEFAULT_SPACING;
import static nl.tudelft.watchdog.core.ui.wizards.WizardUIElements.ID_LENGTH;

public class ProjectRegistrationInputPanel extends JPanel {
    private static final String PROJECT_NAME_TEXTFIELD_TOOLTIP = "The name of the project(s) you work on in this workspace.";
    private static final String PROJECT_WEBSITE_TEXTFIELD_TOOLTIP = "If you have a website, we'd love to see it here.";
    private static final String CI_USAGE_LABEL_TEXT = "Does your project use any Continuous Integration tools (Travis, Jenkins, etc.)?";
    private static final String JUNIT_USAGE_LABEL_TEXT = "  ... unit testing framework (e.g. JUnit)?";
    private static final String MOCKITO_USAGE_LABEL_TEXT = "  ... other testing frameworks (e.g. Mockito)? ";
    private static final String OTHER_TESTING_USAGE_LABEL_TEXT = "  ... other testing forms (e.g. manual testing)? ";
    private static final String PROJECT_CREATION_MESSAGE_SUCCESSFUL = "Your WatchDog User has successfully been created.";
    private static final String PROJECT_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog user.";

    private final JTextField projectName;
    private final JTextField projectWebsite;
    private final ButtonGroup ciUsage;
    private final ButtonGroup junitUsage;
    private final ButtonGroup mockitoUsage;
    private final ButtonGroup otherTestingUsage;
    private final Container buttonContainer;
    private Container statusContainer;

    public ProjectRegistrationInputPanel(Consumer<Boolean> callback) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel inputContainer = new JPanel(new GridLayout(0, 2));
        this.add(inputContainer);

        this.projectName = WizardUIElements.createLinkedLabelTextField("Project name: ", PROJECT_NAME_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.projectWebsite = WizardUIElements.createLinkedLabelTextField("Project website: ", PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.ciUsage = WizardUIElements.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

        inputContainer.add(new JLabel("Does your project use..."));
        inputContainer.add(Box.createGlue());

        this.junitUsage = WizardUIElements.createYesNoDontKnowQuestionWithLabel(JUNIT_USAGE_LABEL_TEXT, inputContainer);
        this.mockitoUsage = WizardUIElements.createYesNoDontKnowQuestionWithLabel(MOCKITO_USAGE_LABEL_TEXT, inputContainer);
        this.otherTestingUsage = WizardUIElements.createYesNoDontKnowQuestionWithLabel(OTHER_TESTING_USAGE_LABEL_TEXT, inputContainer);

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
        Project project = new Project(WatchDogGlobals.getPreferences().getUserId());

        project.name = projectName.getText();
        project.website = projectWebsite.getText();
        project.usesContinuousIntegration = WizardUIElements.getChoiceFromButtonGroup(ciUsage);
        project.usesJunit = WizardUIElements.getChoiceFromButtonGroup(junitUsage);
        project.usesOtherTestingFrameworks = WizardUIElements.getChoiceFromButtonGroup(mockitoUsage);
        project.usesOtherTestingForms = WizardUIElements.getChoiceFromButtonGroup(otherTestingUsage);

        String projectId;

        try {
            projectId = new JsonTransferer().registerNewProject(project);
        } catch (ServerCommunicationException exception) {
            buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
            buttonContainer.add(new JLabel(PROJECT_CREATION_MESSAGE_FAILURE));

            statusContainer.add(WizardUIElements.createErrorMessageLabel(exception));

            return false;
        }

        PreferencesBase preferences = WatchDogGlobals.getPreferences();
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
