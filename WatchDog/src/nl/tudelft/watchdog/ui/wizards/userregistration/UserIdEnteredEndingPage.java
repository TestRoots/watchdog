package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class UserIdEnteredEndingPage extends FinishableWizardPage {

	/** An encouraging message for the end of a sentence. */
	public static final String encouragingEndMessage = "\n\nHappy hours-collecting and prize-winning with WatchDog! \nThe longer you use WatchDog, the higher your chances of winning!";

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
	 * Connects to the server, querying for the user entered on the
	 * {@link UserWelcomePage}, and displays an according wizard page as a
	 * reaction.
	 */
	private void connectToServer() {
		userid = ((UserWelcomePage) getWizard().getStartingPage()).getId();
		String url = NetworkUtils.buildExistingUserURL(userid);
		switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
		case SUCCESSFUL:
			((UserRegistrationWizard) getWizard()).userid = userid;
			setTitle("Welcome back!");
			setDescription("Thanks for using your existing user!");
			setPageComplete(true);
			dynamicComposite = createSuccessWizzard(topComposite);
			break;
		case UNSUCCESSFUL:
		case NETWORK_ERROR:
			setTitle("Wrong user id");
			setErrorMessageAndPageComplete("This user id does not exist.");
			dynamicComposite = createUserNotFoundComposite(topComposite);
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
				"Your user id  "
						+ userid
						+ "  has been registered with this Eclipse installation. You can change the id and other WatchDog settings in the Eclipse preferences."
						+ encouragingEndMessage, composite);
		return composite;
	}

	/** Creates and returns a composite in case of not finding the user. */
	private Composite createUserNotFoundComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel("User not found!", composite);
		UIUtils.createWrappingLabel(
				"We could not find the user id  "
						+ userid
						+ "  on our server. Did you miss-type the id? Or did something go wrong while copy-and-pasting your user id? Please, go back and correct it.",
				composite);
		return composite;
	}

	@Override
	public boolean canFinish() {
		return isPageComplete();
	}

}
