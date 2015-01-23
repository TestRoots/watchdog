package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.wizards.IdEnteredEndingPage;
import nl.tudelft.watchdog.ui.wizards.WelcomePage;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationWizard;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
public class ProjectIdEnteredEndingPage extends IdEnteredEndingPage {

	/** Constructor. */
	public ProjectIdEnteredEndingPage() {
		super("project");
		pageNumber = getWizard() instanceof ProjectRegistrationWizard ? 2 : 4;
	}

	@Override
	protected String getId() {
		if (getWizard() instanceof ProjectRegistrationWizard) {
			return ((ProjectWelcomePage) getWizard().getStartingPage()).getId();
		} else {
			return ((WelcomePage) getWizard().getPreviousPage(this)).getId();
		}
	}

	@Override
	protected String buildTransferURLforId() {
		return NetworkUtils.buildExistingProjectURL(id);
	}

	@Override
	protected void setId() {
		if (getWizard() instanceof ProjectRegistrationWizard) {
			((ProjectRegistrationWizard) getWizard()).projectId = id;
		} else {
			((UserRegistrationWizard) getWizard()).projectId = id;
		}
	}

}
