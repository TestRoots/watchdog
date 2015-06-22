package nl.tudelft.watchdog.ui.wizards.userregistration;

import com.intellij.ide.wizard.CommitStepException;
import nl.tudelft.watchdog.logic.network.NetworkUtils;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.wizards.IdEnteredEndingStepBase;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;


/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class UserIdEnteredEndingStep extends IdEnteredEndingStepBase {

	/** Constructor. */
	public UserIdEnteredEndingStep(int pageNumber, RegistrationWizardBase wizard) {
		super("user", pageNumber, wizard);
	}

	@Override
	protected String buildTransferURLforId() {
		return NetworkUtils.buildExistingUserURL(id);
	}

	@Override
	protected String getId() {

		return ((UserProjectRegistrationWizard) getWizard()).userWelcomeStep.getId();
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

    @Override
    public void commit(CommitType commitType) {

    }
}
