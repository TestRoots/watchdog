package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.wizards.IdEnteredEndingPageBase;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class UserIdEnteredEndingPage extends IdEnteredEndingPageBase {

	/** Constructor. */
	public UserIdEnteredEndingPage(int pageNumber) {
		super("user", pageNumber);
	}

	@Override
	protected String buildTransferURLforId() {
		return NetworkUtils.buildExistingUserURL(id);
	}

	@Override
	protected String getId() {
		return ((UserProjectRegistrationWizard) getWizard()).userWelcomePage.getId();
	}

	@Override
	protected void setId() {
		((UserProjectRegistrationWizard) getWizard()).userid = id;
		Preferences.getInstance().setUserid(id);
	}

	@Override
	public boolean canFinish() {
		return false;
	}

}
