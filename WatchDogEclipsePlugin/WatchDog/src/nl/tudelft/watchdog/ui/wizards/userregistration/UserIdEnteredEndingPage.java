package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.wizards.IdEnteredEndingPage;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class UserIdEnteredEndingPage extends IdEnteredEndingPage {

	/** Constructor. */
	public UserIdEnteredEndingPage() {
		super("user");
	}

	protected String buildTransferURLforId() {
		return NetworkUtils.buildExistingUserURL(id);
	}

	protected void setId() {
		((UserRegistrationWizard) getWizard()).userid = id;
	}

	protected String getId() {
		return ((UserWelcomePage) getWizard().getStartingPage()).getId();
	}

}
