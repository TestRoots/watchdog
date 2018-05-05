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
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.INPUT_IS_OPTIONAL;
import static nl.tudelft.watchdog.intellij.ui.wizards.WizardStep.DEFAULT_SPACING;

class UserRegistrationInputPanel extends RegistrationInputPanel {

    private JTextField email;
    private JTextField company;
    private JComboBox<String> programmingExperience;

    /**
     * A panel to ask the user questions to create their user profile.
     * @param callback The callback invoked after the user clicked "Create WatchDog User".
     */
    UserRegistrationInputPanel(Consumer<Boolean> callback) {
        super(callback);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
                "<h3>" + WATCHDOG_USER_PROFILE + "</h3>" +
                USER_DATA_REQUEST + "<br>" +
                INPUT_IS_OPTIONAL));

		this.inputContainer = new JPanel(new GridLayout(0, 2));
		this.add(inputContainer);

        this.createInputFields();

        this.add(Box.createVerticalStrut(DEFAULT_SPACING));
        this.createButtonAndStatusContainer(CREATE_USER_BUTTON_TEXT);
    }

	private void createInputFields() {
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

    @Override
    boolean registerAction() {
		User user = new User();
        user.email = email.getText();
        user.organization = company.getText();
        user.programmingExperience = (String) this.programmingExperience.getSelectedItem();
        user.operatingSystem = System.getProperty("os.name");

        String userId;

        try {
            userId = new JsonTransferer().registerNewUser(user);
        } catch (ServerCommunicationException exception) {
        	this.createFailureMessage(USER_CREATION_MESSAGE_FAILURE, exception);

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.setUserId(userId);
        preferences.setProgrammingExperience(user.programmingExperience);

        this.createSuccessIdOutput(USER_CREATION_MESSAGE_SUCCESSFUL, YOUR_USER_ID_LABEL, userId);

        return true;
    }
}
