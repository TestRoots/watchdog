package nl.tudelft.watchdog.intellij.ui.new_wizards;

import com.intellij.openapi.ui.ComboBox;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;
import org.apache.commons.lang.WordUtils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.intellij.ui.new_wizards.UserRegistrationStep.ID_LENGTH;
import static nl.tudelft.watchdog.intellij.ui.new_wizards.WizardStep.DEFAULT_SPACING;

class UserRegistrationInputPanel extends JPanel {

    private static final String EMAIL_TEXTFIELD_TOOLTIP = "We will use this e-mail address for future communication (if any).";
    private static final String COMPANY_TEXTFIELD_TOOLTIP = "You can include the website or name of your organisation here.";
    private static final String USER_CREATION_MESSAGE_SUCCESFUL = "Your WatchDog User has succesfully been created.";
    private static final String USER_CREATION_MESSAGE_FAILURE = "Problem creating a new WatchDog user.";
    private final JTextField email;
    private final JTextField company;
    private final JComboBox<String> programmingExperience;
    private final JTextField operatingSystem;
    private final Container buttonContainer;
    private Container statusContainer;

    UserRegistrationInputPanel(Consumer<Boolean> callback) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
                "<h3>WatchDog user registration</h3>" +
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

        this.operatingSystem = WizardStep.createLinkedLabelTextField("Your operating system: ", COMPANY_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.operatingSystem.setText(System.getProperty("os.name"));
        this.operatingSystem.setEditable(false);

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));
        this.buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(buttonContainer);

        this.statusContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(statusContainer);

        JButton button = new JButton("Create new WatchDog user");
        this.buttonContainer.add(button);
        button.addActionListener(actionEvent -> {
            this.statusContainer.removeAll();

            this.buttonContainer.removeAll();
            this.buttonContainer.add(button);

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
        buttonContainer.add(new JLabel(USER_CREATION_MESSAGE_SUCCESFUL));

        statusContainer.add(new JLabel("Your User ID is: "));

        JTextField userIdField = new JTextField("USER-ID", ID_LENGTH);
        userIdField.setEditable(false);
        statusContainer.add(userIdField);

        return true;
    }
}
