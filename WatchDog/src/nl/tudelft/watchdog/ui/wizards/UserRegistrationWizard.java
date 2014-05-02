package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.ui.preferences.Preferences;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * A wizard that allows to register a new user or set an existing user. Does
 * some magic tricks to enable skipping of pages and recalculation of Finish
 * button.
 */
public class UserRegistrationWizard extends Wizard {
	/** The first page in the wizard. */
	WelcomePage welcomePage;

	/** When a user already exists ... */
	UserIdEnteredEndingPage existingUserEndingPage;

	/** Allows a shortcut to the finish button. */
	boolean shortcutToCanFinish = false;

	@Override
	public void addPages() {
		welcomePage = new WelcomePage();
		addPage(welcomePage);
		addPage(new UserRegistrationPage());
		existingUserEndingPage = new UserIdEnteredEndingPage();
		addPage(existingUserEndingPage);
	}

	@Override
	public boolean canFinish() {
		FinishableWizardPage currentPage = (FinishableWizardPage) getContainer()
				.getCurrentPage();
		return currentPage.canFinish();
	}

	@Override
	public boolean performFinish() {
		Preferences.getInstance().getStore()
				.setValue(Preferences.USERID_KEY, welcomePage.getUserId());
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == welcomePage && !welcomePage.getRegisterNewUser()) {
			return existingUserEndingPage;
		}
		return super.getNextPage(page);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == existingUserEndingPage
				&& !welcomePage.getRegisterNewUser()) {
			return welcomePage;
		}
		return super.getPreviousPage(page);
	}
}
