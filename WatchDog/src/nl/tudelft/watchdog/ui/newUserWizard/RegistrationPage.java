package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * The Page on which new users can register themselves.
 */
class RegistrationPage extends WizardPage {

	/** Constructor. */
	protected RegistrationPage() {
		super("Registration Page");
		setTitle("Register with WatchDog!");
		setDescription("Our promise: We keep your data private. From everybody. At all times. If you fill out this page, you help us a lot with our research. If you win one of our amazing prices, we write you an email.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = createRegistrationComposite(parent);

		// Required to avoid an error in the system
		setControl(topComposite);
		// TODO (MMB) Need to change this dynamically!
		setPageComplete(true);
	}

	/** Creates and returns the form of the registration. */
	private Composite createRegistrationComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 2);
		composite.setLayoutData(UIUtils.fullGirdUsageData);

		UIUtils.createLabel("Your Name:", composite);
		UIUtils.createTextInput(composite);
		UIUtils.createLabel("Your eMail:", composite);
		UIUtils.createTextInput(composite);
		UIUtils.createLabel("Your Organization/Company:", composite);
		UIUtils.createTextInput(composite);
		UIUtils.createLabel("Your Project:", composite);
		UIUtils.createTextInput(composite);
		UIUtils.createLabel("Your Role in the Project:", composite);
		UIUtils.createTextInput(composite);

		UIUtils.createLabel("Does your project use JUnit?", composite);
		UIUtils.createTextInput(composite);

		return composite;
	}

}
