package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.openapi.ui.ComboBox;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.User.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.intellij.ui.wizards.UserRegistrationStep.ID_LENGTH;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

class UserRegistrationInputPanel extends JPanel {

    private final Container buttonContainer;
    private final JButton createWatchDogUserButton;
    private JTextField email;
    private JTextField company;
    private JComboBox<String> programmingExperience;
    private JLabel operatingSystem;
    private Container statusContainer;

    /**
     * A panel to ask the user questions to create their user profile.
     * @param callback The callback invoked after the user clicked "Create WatchDog User".
     */
    UserRegistrationInputPanel(Consumer<Boolean> callback) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
                "<h3>" + WATCHDOG_USER_PROFILE + "</h3>" +
                USER_DATA_REQUEST + "<br>" +
                INPUT_IS_OPTIONAL));

        this.createInputFields();

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));
        this.buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(buttonContainer);

        this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(statusContainer);

        this.createWatchDogUserButton = new JButton(CREATE_USER_BUTTON_TEXT);
        this.buttonContainer.add(createWatchDogUserButton);
        createWatchDogUserButton.addActionListener(actionEvent -> {
            this.statusContainer.removeAll();

            this.buttonContainer.removeAll();
            this.buttonContainer.add(createWatchDogUserButton);

            callback.accept(registerUser());
        });
    }

    private void createInputFields() {
        JPanel inputContainer = new JPanel(new GridLayout(0, 2));
        this.add(inputContainer);

        this.email = WizardStep.createLinkedLabelTextField(EMAIL_LABEL, EMAIL_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.company = WizardStep.createLinkedLabelTextField(COMPANY_LABEL, COMPANY_TEXTFIELD_TOOLTIP, 150, inputContainer);

        inputContainer.add(new JLabel(PROGRAMMING_EXPERIENCE_LABEL));

        this.programmingExperience = new ComboBox<>();
        for (String years : PROGRAMMING_EXPERIENCE_YEARS) {
            programmingExperience.addItem(years);
        }
        programmingExperience.setSelectedIndex(0);
        inputContainer.add(programmingExperience);

        inputContainer.add(new JLabel(OPERATING_SYSTEM_LABEL));
        inputContainer.add(new JLabel(System.getProperty("os.name")));
    }

    private boolean registerUser() {
        User user = new User();
        user.email = email.getText();
        user.organization = company.getText();
        user.programmingExperience = (String) this.programmingExperience.getSelectedItem();
        user.operatingSystem = this.operatingSystem.getText();

        String userId;

        try {
            userId = new JsonTransferer().registerNewUser(user);
        } catch (ServerCommunicationException exception) {
            buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
            buttonContainer.add(new JLabel(USER_CREATION_MESSAGE_FAILURE));

            statusContainer.add(WizardStep.createErrorMessageLabel(exception));

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.setUserId(userId);
        preferences.setProgrammingExperience(user.programmingExperience);

        buttonContainer.add(Box.createHorizontalStrut(DEFAULT_SPACING));
        buttonContainer.add(new JLabel(USER_CREATION_MESSAGE_SUCCESSFUL));

        statusContainer.add(new JLabel(YOUR_USER_ID_LABEL));

        JTextField userIdField = new JTextField(userId, ID_LENGTH);
        userIdField.setEditable(false);
        statusContainer.add(userIdField);

        this.createWatchDogUserButton.setEnabled(false);

        return true;
    }
}