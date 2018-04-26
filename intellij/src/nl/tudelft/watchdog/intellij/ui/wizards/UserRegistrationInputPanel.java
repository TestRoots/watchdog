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
import static nl.tudelft.watchdog.intellij.ui.wizards.UserRegistrationStep.ID_LENGTH;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

class UserRegistrationInputPanel extends JPanel {

    private final JTextField email;
    private final JTextField company;
    private final JComboBox<String> programmingExperience;
    private final JLabel operatingSystem;
    private final Container buttonContainer;
    private final JButton createWatchDogUserButton;
    private Container statusContainer;

    UserRegistrationInputPanel(Consumer<Boolean> callback) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
                "<h3>WatchDog user profile</h3>" +
                "Please fill in the following data to create a WatchDog user account for you.<br>" +
                "The input is optional, but greatly appreciated to improve the quality of our research data.<br>"));

        JPanel inputContainer = new JPanel(new GridLayout(0, 2));
        this.add(inputContainer);

        this.email = WizardStep.createLinkedLabelTextField("Your e-mail: ", EMAIL_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.company = WizardStep.createLinkedLabelTextField("Your Organisation/Company: ", COMPANY_TEXTFIELD_TOOLTIP, 150, inputContainer);

        inputContainer.add(new JLabel("Your programming experience: "));

        this.programmingExperience = new ComboBox<>();
        for (String years : new String[]{"N/A", "< 1 year", "1-2 years", "3-6 years", "7-10 years", "> 10 years"}) {
            programmingExperience.addItem(years);
        }
        programmingExperience.setSelectedIndex(0);
        inputContainer.add(programmingExperience);

        JLabel operatingSystemLabel = new JLabel("Your operating system: ");
        operatingSystemLabel.setToolTipText(OPERATING_SYSTEM_TOOLTIP);
        inputContainer.add(operatingSystemLabel);

        this.operatingSystem = new JLabel(System.getProperty("os.name"));
        operatingSystem.setToolTipText(OPERATING_SYSTEM_TOOLTIP);
        inputContainer.add(operatingSystem);

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));
        this.buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(buttonContainer);

        this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(statusContainer);

        this.createWatchDogUserButton = new JButton("Create new WatchDog user");
        this.buttonContainer.add(createWatchDogUserButton);
        createWatchDogUserButton.addActionListener(actionEvent -> {
            this.statusContainer.removeAll();

            this.buttonContainer.removeAll();
            this.buttonContainer.add(createWatchDogUserButton);

            callback.accept(registerUser());
        });
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

        statusContainer.add(new JLabel("Your User ID is: "));

        JTextField userIdField = new JTextField(userId, ID_LENGTH);
        userIdField.setEditable(false);
        statusContainer.add(userIdField);

        this.createWatchDogUserButton.setEnabled(false);

        return true;
    }
}
