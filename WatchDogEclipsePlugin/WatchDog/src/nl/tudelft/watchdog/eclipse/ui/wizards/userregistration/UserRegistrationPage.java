package nl.tudelft.watchdog.eclipse.ui.wizards.userregistration;

import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.User;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;
import nl.tudelft.watchdog.eclipse.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.eclipse.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationEndingPageBase;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

/** The Page on which new users can register themselves. */
public class UserRegistrationPage extends RegistrationEndingPageBase {

	private static final String USER_REGISTRATION_TEXT = "User Registration (2/5)";

	/** The email address entered by the user. */
	private Text emailInput;

	/** The organization entered by the user. */
	private Text organizationInput;

	/** User may be contacted. */
	private Button mayContactButton;

	private Combo experienceDropDown;

	/** Constructor. */
	protected UserRegistrationPage(int pageNumber) {
		super(USER_REGISTRATION_TEXT, pageNumber);
		setTitle(USER_REGISTRATION_TEXT);
		setDescription("Only if you participate, can you win.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = createRegistrationComposite(parent);
		setControl(topComposite);
		setPageComplete(false);
	}

	/** Creates and returns the form of the registration. */
	private Composite createRegistrationComposite(Composite parent) {
		Composite innerParent = UIUtils.createGridedComposite(parent, 1);
		innerParent.setLayoutData(UIUtils.createFullGridUsageData());

		Composite introductionText = UIUtils.createGridedComposite(innerParent,
				1);
		UIUtils.createBoldLabel(
				"We keep your data private. From everybody. Always.",
				introductionText);
		UIUtils.createLabel(
				"By filling out this form, you help us a lot with our research. And you participate in our lottery.",
				introductionText);

		Composite composite = UIUtils.createGridedComposite(innerParent, 2);
		composite.setLayoutData(UIUtils.createFullGridUsageData());

		emailInput = UIUtils.createLinkedFieldInput("Your eMail: ",
				"We contact you via this address, if you win one of our amazing prices. So make sure it's correct.",
				composite);
		organizationInput = UIUtils.createLinkedFieldInput(
				"Your Organization/Company: ",
				"You can include your organization's website here.", composite);

		FormValidationListener formValidator = new FormValidationListener(this);
		emailInput.addModifyListener(formValidator);

		UIUtils.createLabel("Your Programming Experience: ", composite);
		experienceDropDown = UIUtils.createComboList(
				composite, formValidator, new String[] { "", "< 1 year",
						"1-2 years", "3-6 years", "7-10 years", "> 10 years" },
				0);

		mayContactButton = new Button(innerParent, SWT.CHECK);
		mayContactButton.setText(
				"I want to win prizes! The lovely TestRoots team from TU Delft may contact me.");
		mayContactButton.addSelectionListener(formValidator);
		mayContactButton.setSelection(true);

		UIUtils.createLabel("", innerParent);
		UIUtils.createLabel(
				"You can stay anonymous. But please consider registering, you can win prizes!",
				innerParent);

		return innerParent;
	}

	@Override
	public void validateFormInputs() {
		setErrorMessageAndPageComplete(null);
		if (WatchDogUtils
				.isEmptyOrHasOnlyWhitespaces(getProgrammingExperience())) {
			setErrorMessageAndPageComplete(
					"Please fill in your years of programming experience");
		}

		if (!WatchDogUtils.isEmpty(emailInput.getText())) {
			if (!EmailValidator.getInstance(false)
					.isValid(emailInput.getText())) {
				setErrorMessageAndPageComplete(
						"Your mail address is not valid!");
			}
		}

		if (WatchDogUtils.isEmpty(emailInput.getText())
				&& mayContactButton.getSelection()) {
			setErrorMessageAndPageComplete(
					"You can only participate in the lottery if you enter your email address.");
		}

		getWizard().getContainer().updateButtons();
	}

	protected void makeRegistration() {
		User user = new User();
		user.email = getEmailInput().getText();
		user.organization = getOrganizationInput().getText();
		user.mayContactUser = getMayContactUser();
		user.programmingExperience = getProgrammingExperience();
		user.operatingSystem = Platform.getOS();

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
		preferences.registerProjectId(WatchDogUtils.getWorkspaceName(), "");
		preferences.setProgrammingExperience(user.programmingExperience);
	}

	/**
	 * Creates report of the user registration to the {@link Composite} parent.
	 */
	public void createUserRegistrationSummary(Composite parent) {
		if (successfulRegistration) {
			FinishableWizardPage.createSuccessMessage(parent, messageTitle,
					messageBody, id);
		} else {
			FinishableWizardPage.createFailureMessage(parent, messageTitle,
					messageBody);
		}
	}

	/** @return the email */
	public Text getEmailInput() {
		return emailInput;
	}

	/** @return the organization */
	public Text getOrganizationInput() {
		return organizationInput;
	}

	/** @return the programming experience in years. */
	public String getProgrammingExperience() {
		return experienceDropDown.getText();
	}

	/**
	 * @return whether the user may be contacted. (If this is false, no lottery
	 *         participation.)
	 */
	public boolean getMayContactUser() {
		return mayContactButton.getSelection();
	}

	@Override
	public boolean canFinish() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			validateFormInputs();
		} else {
			if (isPageComplete() && getWizard().getStartingPage() != getWizard()
					.getContainer().getCurrentPage()) {
				makeRegistration();
			}
		}
	}
}
