package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.wizards.IdEnteredEndingPage;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class ProjectIdEnteredEndingPage extends IdEnteredEndingPage {

	/** Constructor. */
	public ProjectIdEnteredEndingPage() {
		super("project");
	}

	@Override
	protected String getId() {
		return ((ProjectWelcomePage) getWizard().getStartingPage()).getId();
	}

	@Override
	protected String buildTransferURLforId() {
		return NetworkUtils.buildExistingProjectURL(id);
	}

	@Override
	protected void setId() {
		((ProjectRegistrationWizard) getWizard()).projectId = id;
	}

}
