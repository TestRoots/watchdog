package nl.tudelft.watchdog.intellij.ui.wizards.userregistration;

import com.intellij.openapi.ui.ComboBox;
import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.intellij.ui.wizards.*;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import org.apache.commons.validator.routines.EmailValidator;

import javax.swing.*;

/**
 * The Page on which new users can register themselves.
 */
public class UserRegistrationStep extends RegistrationEndingStepBase {

    private static final String USER_REGISTRATION_TEXT = "User Registration Form";

    /**
     * The email address entered by the user.
     */
    private JTextField emailInput;

    /**
     * The organization entered by the user.
     */
    private JTextField organizationInput;

    /**
     * User may be contacted.
     */
    private JCheckBox mayContactButton;

    private ComboBox experienceDropDown;

    /**
     * Constructor.
     */
    public UserRegistrationStep(int pageNumber, RegistrationWizardBase wizard) {
        super(USER_REGISTRATION_TEXT, pageNumber, wizard);
        setTitle(USER_REGISTRATION_TEXT);
        descriptionText = "Only if you participate, can you win.";
    }

    /**
     * Creates and returns the registration form.
     */
    private void createRegistrationForm(JPanel parent) {
        JPanel introductionText = UIUtils.createGridedJPanel(parent, 1);
        UIUtils.createBoldLabel(
                introductionText,
                "We keep your data private. From everybody. Always.");
        UIUtils.createLabel(
                introductionText,
                "By filling out this form, you help us a lot with our research. And you participate in our lottery.");
        UIUtils.createLabel(introductionText, "");

        JPanel middlePanel = UIUtils.createGridedJPanel(parent, 2);

        emailInput = UIUtils
                .createLinkedFieldInput(middlePanel,
                        "Your eMail: ", 20,
                        "We contact you via this address, if you win one of our amazing prices. So make sure it's correct.");
        organizationInput = UIUtils.createLinkedFieldInput(
                middlePanel, "Your Organization/Company: ",
                20, "You can include your organization's website here.");
        FormValidationListener formValidator = new FormValidationListener(this);
        emailInput.getDocument().addDocumentListener(formValidator);

        UIUtils.createLabel(middlePanel, "Your Programming Experience: ");
        experienceDropDown = UIUtils.createComboBox(middlePanel, formValidator,
                new String[]{"", "< 1 year", "1-2 years", "3-6 years",
                        "7-10 years", "> 10 years"}, 0);

        JPanel bottomPanel = UIUtils.createGridedJPanel(parent, 1);
        mayContactButton = UIUtils.createCheckBox(bottomPanel, "I want to win prizes! The lovely TestRoots team from TU Delft may contact me.");
        mayContactButton.addItemListener(formValidator);
        mayContactButton.setSelected(true);
        UIUtils.createLabel(bottomPanel, "");
        UIUtils.createLabel(bottomPanel, "You can stay anonymous. But please consider registering, you can win prizes!");

    }

    @Override
    public void validateFormInputs() {
        setErrorMessageAndStepComplete(null);
        if (WatchDogUtils
                .isEmptyOrHasOnlyWhitespaces(getProgrammingExperience())) {
            setErrorMessageAndStepComplete("Please fill in your years of programming experience");
        }

        if (!WatchDogUtils.isEmpty(emailInput.getText())) {
            if (!EmailValidator.getInstance(false)
                    .isValid(emailInput.getText())) {
                setErrorMessageAndStepComplete("Your mail address is not valid!");
            }
        }

        if (WatchDogUtils.isEmpty(emailInput.getText())
                && mayContactButton.isSelected()) {
            setErrorMessageAndStepComplete("You can only participate in the lottery if you enter your email address.");
        }

        updateStep();
    }

    protected void makeRegistration() {
        User user = new User();
        user.email = getEmailInput().getText();
        user.organization = getOrganizationInput().getText();
        user.mayContactUser = getMayContactUser();
        user.programmingExperience = getProgrammingExperience();
        user.operatingSystem = System.getProperty("os.name");

        try {
            id = new JsonTransferer().registerNewUser(user);
        } catch (ServerCommunicationException exception) {
            successfulRegistration = false;
            messageTitle = "Problem creating new user!";
            messageBody = exception.getMessage();
            return;
        }

        successfulRegistration = true;
        ((UserProjectRegistrationWizard) getWizard()).userid = id;
        messageTitle = "New user registered!";
        messageBody = "Your new user id is registered: ";

        Preferences preferences = Preferences.getInstance();
        preferences.setUserId(id);
        preferences.registerProjectId(WatchDogUtils.getProjectName(), "");
        preferences.setProgrammingExperience(user.programmingExperience);
    }

    /**
     * Creates report of the user registration in the {@link JPanel} parent.
     */
    public void createUserRegistrationSummary(JPanel parent) {
        if (successfulRegistration) {
            WizardStep.createSuccessMessage(parent, messageTitle, messageBody, id);
        } else {
            WizardStep.createFailureMessage(parent, messageTitle, messageBody);
        }
    }

    /**
     * @return the email
     */
    public JTextField getEmailInput() {
        return emailInput;
    }

    /**
     * @return the organization
     */
    public JTextField getOrganizationInput() {
        return organizationInput;
    }

    /**
     * @return the programming experience in years.
     */
    public String getProgrammingExperience() {
        return experienceDropDown.getSelectedItem().toString();
    }

    /**
     * @return whether the user may be contacted. (If this is false, no lottery
     * participation.)
     */
    public boolean getMayContactUser() {
        return mayContactButton.isSelected();
    }

    @Override
    public boolean canFinish() {
        return false;
    }

    @Override
    public void commit(CommitType commitType) {
        if (commitType == CommitType.Next) {
            if (isComplete()) {
                makeRegistration();
            }
        }
    }


    @Override
    public void _init() {
        super._init();
        JPanel oneColumn = UIUtils.createVerticalBoxJPanel(topPanel);
        createHeader(oneColumn);
        createRegistrationForm(oneColumn);
        validateFormInputs();
    }
}
