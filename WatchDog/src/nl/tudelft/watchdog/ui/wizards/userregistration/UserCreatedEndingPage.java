package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.logic.ServerCommunicationException;
import nl.tudelft.watchdog.logic.User;
import nl.tudelft.watchdog.logic.interval.JsonTransferer;
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

	private Composite dynamicComposite;

	/**
	 * The user id (either as retrieved from the previous page or as freshly
	 * accepted from the server).
	 */
	private String userid = "";

	private String messageTitle;

	private boolean successfulUserRegistration = false;

	private String messageBody;

	/** Constructor. */
	protected UserCreatedEndingPage() {
		super("User created.");
	}

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("", topComposite);
		setControl(topComposite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (dynamicComposite != null) {
				dynamicComposite.dispose();
			}
			createUser();
			createPageContent();
			dynamicComposite.layout(true);
			topComposite.layout(true);
		}
	}

	private void createUser() {
		UserRegistrationPage page = ((UserRegistrationWizard) getWizard()).userRegistrationPage;
		User user = new User();
		user.email = page.getEmailInput().getText();
		user.organization = page.getOrganizationInput().getText();
		user.group = page.getGroupInput().getText();
		user.mayContactUser = page.getMayContactUser();

		try {
			userid = new JsonTransferer().sendUserRegistration(user);
		} catch (ServerCommunicationException exception) {
			successfulUserRegistration = false;
			messageTitle = "Problem creating new user!";
			messageBody = exception.getMessage();
			return;
		}

		successfulUserRegistration = true;
		((UserRegistrationWizard) getWizard()).userid = userid;
		messageTitle = "New user registered!";
		messageBody = "Your new user id "
				+ userid
				+ " is registered.\nIf you ever have to, you can change other WatchDog settings in the Eclipse preferences."
				+ UserIdEnteredEndingPage.encouragingEndMessage;
	}

	private void createPageContent() {
		dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
		dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());

		UIUtils.createBoldLabel(messageTitle, dynamicComposite);
		setTitle(messageTitle);
		UIUtils.createLabel(messageBody, dynamicComposite);
	}

	@Override
	public boolean canFinish() {
		return successfulUserRegistration;
	}

}
