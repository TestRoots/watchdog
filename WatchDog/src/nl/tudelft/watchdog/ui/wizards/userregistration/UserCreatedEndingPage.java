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
		topComposite.setLayoutData(UIUtils.createFullGridUsageData());

		setControl(topComposite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			createUser();
			UIUtils.createBoldLabel(messageTitle, topComposite);
			UIUtils.createLabel(messageBody, topComposite);
		}
	}

	private void createUser() {
		UserRegistrationPage page = ((UserRegistrationWizard) getWizard()).userRegistrationPage;
		User user = new User();
		user.email = page.getEmailInput().getText();
		user.organization = page.getOrganizationInput().getText();
		user.group = page.getGroupInput().getText();

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
		messageBody = "Your user id "
				+ userid
				+ " has been registered with this Eclipse installation.\nYou can change the id and other WatchDog settings in the Eclipse preferences."
				+ UserIdEnteredEndingPage.encouragingEndMessage;
	}

	@Override
	public boolean canFinish() {
		return successfulUserRegistration;
	}

}
