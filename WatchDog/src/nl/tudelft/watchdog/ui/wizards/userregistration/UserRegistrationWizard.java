package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * A wizard that allows to register a new user or set an existing user. Does
 * some magic tricks to enable skipping of pages and recalculation of Finish
 * button.
 */
public class UserRegistrationWizard extends Wizard {

	/** The first page in the wizard. */
	UserWelcomePage welcomePage;

	/** When a user already exists ... */
	UserIdEnteredEndingPage existingUserEndingPage;

	/** Allows a shortcut to the finish button. */
	boolean shortcutToCanFinish = false;

	private UserCreatedEndingPage userCreatedEndingPage;

	private UserRegistrationPage userRegistrationPage;

	@Override
	public void addPages() {
		welcomePage = new UserWelcomePage();
		addPage(welcomePage);
		userRegistrationPage = new UserRegistrationPage();
		addPage(userRegistrationPage);
		existingUserEndingPage = new UserIdEnteredEndingPage();
		addPage(existingUserEndingPage);
		userCreatedEndingPage = new UserCreatedEndingPage();
		addPage(userCreatedEndingPage);
	}

	@Override
	public boolean performFinish() {
		Preferences.getInstance().getStore()
				.setValue(Preferences.USERID_KEY, welcomePage.getId());
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == welcomePage && !welcomePage.getRegisterNewId()) {
			return existingUserEndingPage;
		}
		if (currentPage == existingUserEndingPage) {
			return null;
		}
		if (currentPage == userRegistrationPage) {
			return userCreatedEndingPage;
		}
		return super.getNextPage(page);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == existingUserEndingPage
				&& !welcomePage.getRegisterNewId()) {
			return welcomePage;
		}
		if (currentPage == userCreatedEndingPage) {
			return null;
		}
		return super.getPreviousPage(page);
	}

	@Override
	public boolean canFinish() {
		FinishableWizardPage currentPage = (FinishableWizardPage) getContainer()
				.getCurrentPage();
		return currentPage.canFinish();
	}

}
