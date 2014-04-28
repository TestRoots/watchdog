package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The Page on which new users can register themselves.
 */
class UserRegistrationPage extends WizardPage {

	private Text emailInput;
	private Text organizationInput;
	private Text groupInput;

	private Composite introductionText;
	private Composite innerParent;
	private Label introLabel;

	/** Constructor. */
	protected UserRegistrationPage() {
		super("Registration Page");
		setTitle("Register with WatchDog!");
		setDescription("");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = createRegistrationComposite(parent);

		// Required to avoid an error in the system
		setControl(topComposite);
		setPageComplete(true);
	}

	/** Creates and returns the form of the registration. */
	private Composite createRegistrationComposite(Composite parent) {
		innerParent = UIUtils.createGridedComposite(parent, 1);
		innerParent.setLayoutData(UIUtils.createFullGridUsageData());

		introductionText = UIUtils.createGridedComposite(innerParent, 1);
		introLabel = UIUtils.createBoldLabel(
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
						"We send you an email to this address, if you win one of our amazing prices. Nothing else.",
						composite);

		organizationInput = UIUtils
				.createLinkedFieldInput(
						"Your Organization/Company: ",
						"You can also include your organization's website, if you like.",
						composite);

		groupInput = UIUtils.createLinkedFieldInput("Your Group (if any): ",
				"If you are not part of a group, please leave this empty.",
				composite);

		return innerParent;
	}

}
