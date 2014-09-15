package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/** The Page on which new users can register themselves. */
class UserRegistrationPage extends FinishableWizardPage {

	private static final String userRegistration = "User Registration (3/3)";

	/** The email address entered by the user. */
	private Text emailInput;

	/** The organization entered by the user. */
	private Text organizationInput;

	/** The group entered by the user. */
	private Text groupInput;

	/** User may be contacted. */
	private Button mayContactButton;

	private Combo experienceDropDown;

	/** Constructor. */
	protected UserRegistrationPage() {
		super(userRegistration);
		setTitle(userRegistration);
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

		emailInput = UIUtils
				.createLinkedFieldInput(
						"Your eMail: ",
						"We contact you via this address, if you win one of our amazing prices. So make sure it's correct.",
						composite);
		organizationInput = UIUtils.createLinkedFieldInput(
				"Your Organization/Company: ",
				"You can include your organization's website here.", composite);
		groupInput = UIUtils.createLinkedFieldInput("Your Group (if any): ",
				"If you are not part of a group, please leave this empty.",
				composite);
		FormValidationListener formValidator = new FormValidationListener(this);
		emailInput.addModifyListener(formValidator);

		UIUtils.createLabel("Your Programming Experience: ", composite);
		experienceDropDown = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		experienceDropDown.add("< 1 year");
		experienceDropDown.add("1-2 years");
		experienceDropDown.add("3-6 years");
		experienceDropDown.add("7-10 years");
		experienceDropDown.add("> 10 years");
		experienceDropDown.addModifyListener(formValidator);

		mayContactButton = new Button(innerParent, SWT.CHECK);
		mayContactButton
				.setText("I want to win prizes! The lovely TestRoots team from TU Delft may contact me.");
		mayContactButton.addSelectionListener(formValidator);
		mayContactButton.setSelection(true);

		UIUtils.createLabel("", innerParent);
		UIUtils.createLabel(
				"You can stay anonymous (by leaving everything empty). But please consider registering, you can win prizes!",
				innerParent);

		return innerParent;
	}

	@Override
	public void validateFormInputs() {
		setErrorMessageAndPageComplete(null);
		if (!WatchDogUtils.isEmpty(emailInput.getText())) {
			if (!EmailValidator.getInstance(false)
					.isValid(emailInput.getText())) {
				setErrorMessageAndPageComplete("Your mail address is not valid!");
			}
		}
		if (WatchDogUtils.isEmpty(emailInput.getText())
				&& mayContactButton.getSelection()) {
			setErrorMessageAndPageComplete("You can only participate in the lottery if you enter your email address.");
		}

		if (WatchDogUtils
				.isEmptyOrHasOnlyWhitespaces(getProgrammingExperience())) {
			setErrorMessage("Please fill in your years of programming experience");
		}

		getWizard().getContainer().updateButtons();
	}

	/** @return the email */
	public Text getEmailInput() {
		return emailInput;
	}

	/** @return the organization */
	public Text getOrganizationInput() {
		return organizationInput;
	}

	/** @return the group */
	public Text getGroupInput() {
		return groupInput;
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
		}
	}
}
