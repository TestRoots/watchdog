package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/** The Page on which new users can register themselves. */
class UserRegistrationPage extends FinishableWizardPage {

	/** A universal listener that reacts on form modification events. */
	private class FormEvaluationListener implements ModifyListener,
			SelectionListener {
		@Override
		public void modifyText(ModifyEvent e) {
			evaluateFormInputs();
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			evaluateFormInputs();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

	/** The email address entered by the user. */
	private Text emailInput;

	/** The organization entered by the user. */
	private Text organizationInput;

	/** The group entered by the user. */
	private Text groupInput;

	/** User may be contacted. */
	private Button mayContactButton;

	/** Constructor. */
	protected UserRegistrationPage() {
		super("Registration Page");
		setTitle("Register with WatchDog!");
		setDescription("Only he who participates, can win!");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = createRegistrationComposite(parent);
		setControl(topComposite);
		setPageComplete(true);
	}

	/** Creates and returns the form of the registration. */
	private Composite createRegistrationComposite(Composite parent) {
		Composite innerParent = UIUtils.createGridedComposite(parent, 1);
		innerParent.setLayoutData(UIUtils.createFullGridUsageData());

		Composite introductionText = UIUtils.createGridedComposite(innerParent,
				1);
		UIUtils.createBoldLabel(
				"We keep your user data private. From everybody. Always.",
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

		emailInput.addModifyListener(new FormEvaluationListener());

		mayContactButton = new Button(innerParent, SWT.CHECK);
		mayContactButton
				.setText("I want to win prizes! The lovely TestRoots team from TU Delft may contact me.");
		mayContactButton.setOrientation(SWT.BOTTOM);
		mayContactButton.setAlignment(SWT.BOTTOM);
		mayContactButton.addSelectionListener(new FormEvaluationListener());
		mayContactButton.setSelection(true);

		UIUtils.createLabel("", innerParent);
		UIUtils.createLabel(
				"You can stay anonymous (by leaving everything empty). But please consider registering, you can win prizes!",
				innerParent);

		return innerParent;
	}

	/** Evaluates whether the form has correct input methods. */
	private void evaluateFormInputs() {
		if (!emailInput.getText().isEmpty()) {
			if (!EmailValidator.getInstance(false)
					.isValid(emailInput.getText())) {
				setErrorMessageAndPageComplete("Your mail address is not valid!");
			} else {
				setErrorMessageAndPageComplete(null);
			}
		} else if (emailInput.getText().isEmpty()
				&& mayContactButton.getSelection()) {
			setErrorMessageAndPageComplete("You can only participate in the lottery if you enter your email address.");
		} else {
			setErrorMessageAndPageComplete(null);
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

	@Override
	boolean canFinish() {
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			evaluateFormInputs();
		}
	}
}
