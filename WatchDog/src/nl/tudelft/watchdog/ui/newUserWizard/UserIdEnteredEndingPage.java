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

	/** The top-level composite. */
	private Composite topComposite;

	/** The dynamic composite. */
	private Composite dynamicComposite;

	/**
	 * The user id (either as retrieved from the previous page or as freshly
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
		switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
		case SUCCESSFUL:
			setTitle("Welcome back!");
			setDescription("Thanks for re-using your existing user!");
			setPageComplete(true);
			canFinish = true;

			dynamicComposite = createSuccessWizzard(topComposite);
			break;
		case UNSUCCESSFUL:
			setTitle("Wrong user id");
			setErrorMessage("This user id does not exist.");
			setPageComplete(false);
			canFinish = false;

			dynamicComposite = createUserNotFoundComposite(topComposite);
			break;
		case NETWORK_ERROR:
			setTitle("WatchDog Server not reachable");
			setDescription("There was an error contacting our server.");
			setPageComplete(true);
			canFinish = true;

			dynamicComposite = createConnectionFailureComposite(topComposite);
			break;
		}
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
	private Composite createSuccessWizzard(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
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
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel("User not found!", composite);
		UIUtils.createWrappingLabel(
				"We could not find the user id "
						+ userid
						+ " on our server. Did you miss-type the id? Or did something go wrong while copy-and-pasting your user id? Please, go back and correct it, or create a new user.",
				composite);
		return composite;
	}

	/** Creates and returns a composite in case of unsuccessful input. */
	private Composite createConnectionFailureComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel("WatchDog server not reached!", composite);
		UIUtils.createWrappingLabel(
				"We could not contact our server. Are you behind a firewall? Are you connected to the internet at all? If there is an issue with your connection that you can fix quickly, you can go back and try again.\n\nIf not: We've registered your user id with this Eclipse installation. Even if you never have (proper) Internet access, you can still use WatchDog. It will store all data on your computer, and you can export it and send it to us manually via email. In this case, please just make sure that you created the user via the new user dialog.",
				composite);
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
