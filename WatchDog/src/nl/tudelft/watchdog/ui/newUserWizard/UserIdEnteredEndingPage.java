package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class UserIdEnteredEndingPage extends WizardPage {

	/** Flag denoting whether the wizard may be finished. */
	boolean canFinish = false;
	private Composite topComposite;
	private Composite dynamicComposite;

	/**
	 * The user id (either as retreived from the previous page or as freshly
	 * accepted from the server).
	 */
	private String userid;

	/** Constructor. */
	protected UserIdEnteredEndingPage() {
		super("Existing user page");
	}

	/**
	 * 
	 */
	private void connectToServer() {
		userid = ((WelcomePage) getPreviousPage()).getUserId();
		String url = NetworkUtils.buildUserURL(userid);
		if (NetworkUtils.urlExists(url)) {
			dynamicComposite = createSuccessComposite(topComposite);
			setPageComplete(true);
			canFinish = true;
		} else {
			dynamicComposite = createUserNotFoundComposite(topComposite);
			setErrorMessage("This user id does not exist.");
			setTitle("Wrong user id.");
			canFinish = false;
		}
		// TODO (MMB) add catch clause
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (dynamicComposite != null) {
				dynamicComposite.dispose();
			}
			connectToServer();
			dynamicComposite.layout(true);
			topComposite.layout(true);
		}
	};

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("", topComposite);
		setControl(topComposite);
	}

	/**
	 * Creates and returns a composite in case of successful verification of
	 * user existence.
	 */
	private Composite createSuccessComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		setTitle("Welcome back!");
		setDescription("Thanks for re-using your existing user!");
		UIUtils.createBoldLabel("Everything worked perfectly.", composite);
		UIUtils.createWrappingLabel(
				"Your user id "
						+ userid
						+ " has been registered with this Eclipse installation. You can change the id and other WatchDog settings in the Eclipse preferences.",
				composite);
		return composite;
	}

	/** Creates and returns a composite in case of not finding the user. */
	private Composite createUserNotFoundComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createBoldLabel("User not found!", composite);
		UIUtils.createWrappingLabel(
				"Did you miss-type the user id? Or did something go wrong while copy-and-pasting your user id? Please, go back to correct it or to create a new user.",
				composite);
		return composite;
	}

	/** Creates and returns a composite in case of unsuccessful input. */
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
