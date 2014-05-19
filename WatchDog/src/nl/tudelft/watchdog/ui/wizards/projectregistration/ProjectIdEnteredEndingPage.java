package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.NetworkUtils;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class ProjectIdEnteredEndingPage extends FinishableWizardPage {

	/** An encouraging message for the end of a sentence. */
	static final String encouragingEndMessage = "\n\nHappy hours-collecting and prize-winning with WatchDog!";

	/** The top-level composite. */
	private Composite topComposite;

	/** The dynamic composite. */
	private Composite dynamicComposite;

	/**
	 * The user id (either as retrieved from the previous page or as freshly
	 * accepted from the server).
	 */
	private String projectid;

	/** Constructor. */
	protected ProjectIdEnteredEndingPage() {
		super("Existing user page");
	}

	/**
	 * Connects to the server, querying for the user entered on the
	 * {@link UserWelcomePage}, and displays an according wizard page as a
	 * reaction.
	 */
	private void connectToServer() {
		projectid = ((ProjectWelcomePage) getWizard().getStartingPage())
				.getId();
		String url = NetworkUtils.buildProjectURL(projectid);
		switch (NetworkUtils.urlExistsAndReturnsStatus200(url)) {
		case SUCCESSFUL:
			setTitle("Welcome back!");
			setDescription("Thanks for re-using your existing project!");
			setPageComplete(true);
			dynamicComposite = createSuccessWizzard(topComposite);
			break;
		case UNSUCCESSFUL:
			setTitle("Wrong project id");
			setErrorMessageAndPageComplete("This id does not exist.");
			dynamicComposite = createUserNotFoundComposite(topComposite);
			break;
		case NETWORK_ERROR:
			setTitle("WatchDog Server not reachable");
			setDescription("There was an error contacting our server.");
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
				"Your project id "
						+ projectid
						+ " has been registered with this Eclipse installation. You can change the id and other WatchDog settings in the Eclipse preferences."
						+ encouragingEndMessage, composite);
		return composite;
	}

	/** Creates and returns a composite in case of not finding the user. */
	private Composite createUserNotFoundComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createBoldLabel("Project not found!", composite);
		UIUtils.createWrappingLabel(
				"We could not find the project id "
						+ projectid
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
				"We could not contact our server. Are you behind a firewall? Are you connected to the internet at all? If there is an issue with your connection that you can fix quickly, you can go back and try again.\n\nIf not: We've registered your project id with this Eclipse installation. Even if you never have (proper) Internet access, you can still use WatchDog. It will store all data on your computer, and you can export it and send it to us manually via email. In this case, please just make sure that you created the user via the new user dialog."
						+ encouragingEndMessage, composite);
		return composite;
	}

	@Override
	public boolean canFinish() {
		return isPageComplete();
	}

}
