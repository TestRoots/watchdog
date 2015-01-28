package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.wizards.IdEnteredEndingPageBase;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
public class ProjectIdEnteredEndingPage extends IdEnteredEndingPageBase {

	/** Constructor. */
	public ProjectIdEnteredEndingPage(int pageNumber) {
		super("project", pageNumber);
	}

	@Override
	protected String buildTransferURLforId() {
		return NetworkUtils.buildExistingProjectURL(id);
	}

	@Override
	protected String getId() {
		return ((RegistrationWizardBase) getWizard()).projectWelcomePage.getId();
	}

	@Override
	protected void setId() {
		((RegistrationWizardBase) getWizard()).setProjectId(id);
	}

}
