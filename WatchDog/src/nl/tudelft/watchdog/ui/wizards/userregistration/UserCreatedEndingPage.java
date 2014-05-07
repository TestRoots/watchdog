package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class UserCreatedEndingPage extends FinishableWizardPage {

	/** The top-level composite. */
	private Composite topComposite;

	/**
	 * The user id (either as retrieved from the previous page or as freshly
	 * accepted from the server).
	 */
	private String userid = "XXXXXXXXXXXXXXXXXXX";

	/** Constructor. */
	protected UserCreatedEndingPage() {
		super("User created.");
	}

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		topComposite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel("Everything worked perfectly.", topComposite);
		UIUtils.createLabel(
				"Your user id "
						+ userid
						+ " has been registered with this Eclipse installation.\nYou can change the id and other WatchDog settings in the Eclipse preferences."
						+ UserIdEnteredEndingPage.encouragingEndMessage,
				topComposite);
		setControl(topComposite);
	}

	@Override
	public boolean canFinish() {
		return true;
	}

}
