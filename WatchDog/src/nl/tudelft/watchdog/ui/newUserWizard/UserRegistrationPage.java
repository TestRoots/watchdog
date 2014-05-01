package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * The Page on which new users can register themselves.
 */
class UserRegistrationPage extends WizardPage {

	private Text emailInput;

	private Text organizationInput;

	private Text groupInput;

	/** Constructor. */
	protected UserRegistrationPage() {
		super("Registration Page");
		setTitle("Register with WatchDog!");
		setDescription("");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = createRegistrationComposite(parent);
		setDescription("By filling out this form, you help us a lot with our research. And you participate in our lottery.");

		// Required to avoid an error in the system
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

		emailInput.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!EmailValidator.getInstance(false).isValid(
						emailInput.getText())) {
					setErrorMessage("Your mail address is not valid!");
				} else {
					setErrorMessage(null);
				}
			}
		});
		UIUtils.createLabel(
				"You can stay anonymous. But please consider registering (you can win prizes!).",
				innerParent);

		return innerParent;
	}
}
