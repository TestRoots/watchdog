package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * The Page on which new users can register themselves.
 */
class UserRegistrationPage extends WizardPage {

	/** Constructor. */
	protected UserRegistrationPage() {
		super("Registration Page");
		setTitle("Register with WatchDog!");
		setDescription("Our promise: We keep your user data private. From everybody. Always. By filling out this form, you help us a lot with our research. We send you an email, if you win one of our amazing prices.");
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
		Composite composite = UIUtils.createGridedComposite(parent, 2);
		composite.setLayoutData(UIUtils.fullGirdUsageData);

		UIUtils.createLabel("Your eMail:", composite);
		UIUtils.createTextInput(composite);
		UIUtils.createLabel("Your Organization/Company:", composite);
		UIUtils.createTextInput(composite);
		UIUtils.createLabel("Your Group:", composite);
		UIUtils.createTextInput(composite);

		return composite;
	}

}
