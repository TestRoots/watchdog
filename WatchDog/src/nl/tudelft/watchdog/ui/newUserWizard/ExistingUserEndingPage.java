package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class ExistingUserEndingPage extends WizardPage {

	/** Flag denoting whether the wizard may be finished. */
	boolean canFinish = false;
	private Composite successComposite;
	private Composite failureComposite;

	/** Constructor. */
	protected ExistingUserEndingPage() {
		super("Existing user page");
	}

	/**
	 * 
	 */
	private void connectToServer() {
		FirstPage page = (FirstPage) getPreviousPage();
		String url = NetworkUtils.buildUserURL(page.getUserId());
		if (NetworkUtils.urlExists(url)) {
			setPageComplete(true);
			canFinish = true;
			successComposite.setVisible(true);
			failureComposite.setVisible(false);

		} else {
			setErrorMessage("The user id does not exist on our server. Go back and correct it, or create a new user.");
			canFinish = false;
			failureComposite.setVisible(true);
			successComposite.setVisible(false);
		}
		// TODO (MMB) add catch clause
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			connectToServer();
		}
	};

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = UIUtils.createGridedComposite(parent, 1);
		successComposite = createSuccessComposite(topComposite);
		failureComposite = createUserNotFoundComposite(topComposite);
		setControl(topComposite);
	}

	/**
	 * Creates and returns a composite in case of successful verification of
	 * user existance.
	 */
	private Composite createSuccessComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		// TODO (MMB) Add personal name here from Json?
		UIUtils.createLabel("Welcome back!", composite);
		UIUtils.createLabel(
				"Everything worked perfectly. WatchDog will now silently run in the background. We will never both you again. Except if you win one of our prizes, of course.",
				composite);
		composite.setVisible(false);
		return composite;
	}

	/** Creates and returns a composite in case of not finding the user. */
	private Composite createUserNotFoundComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("User not found!", composite);
		UIUtils.createLabel(
				"Did you miss-type the userid? Or did something go wrong while copy-and-pasting your userid? You can go back and see if there's a problem with the id you posted. If not, you can contact us via email to info@testroots.org. If you can remember some of the details of your last registration (such as name, email address or similar) we will try to help find your existing userid.",
				composite);
		composite.setVisible(false);
		return composite;
	}

	/**
	 * Creates and returns a composite in case of unsuccessful
	 */
	private Composite createConnectionFailureComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("No Connection made!", composite);
		UIUtils.createLabel(
				"You could not contact our server. Our server might be down temporarily. Or you might have a firewall? Or you might not have internet yourself. Hard to tell from here. Anyway, we stored the userid you entered and will try to work with it. If you never have internet, do not dispair: You can still use WatchDog. It will cache all data, and then you can export it and send it to us via email. In that case, please just make sure that you created the user via the dialog first.",
				composite);
		composite.setVisible(false);
		return composite;
	}

	/**
	 * @return Whether the overall wizard is finish-able, based on the
	 *         connection tries made from this page.
	 */
	/* package */boolean canFinish() {
		return canFinish;
	}

}
