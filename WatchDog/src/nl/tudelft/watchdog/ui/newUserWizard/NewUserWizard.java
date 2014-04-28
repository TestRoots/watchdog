package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.preferences.Preferences;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * A wizard that allows to register a new user or set an existing user. Does
 * some magic tricks to enable skipping of pages and recalculation of Finish
 * button.
 */
public class NewUserWizard extends Wizard {
	/** The first page in the wizard. */
	FirstPage firstPage;

	/** When a user already exists ... */
	ExistingUserEndingPage existingUserEndingPage;

	/** Allows a shortcut to the finish button. */
	boolean shortcutToCanFinish = false;

	@Override
	public void addPages() {
		firstPage = new FirstPage();
		addPage(firstPage);
		addPage(new UserRegistrationPage());
	}

	@Override
	public boolean canFinish() {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == firstPage) {
			if (firstPage.hasValidUserId()) {
				return true;
			}
			return false;
		}
		if (getContainer().getCurrentPage() == existingUserEndingPage
				&& existingUserEndingPage.canFinish()) {
			return true;
		}
		return super.canFinish();
	}

	@Override
	public boolean performFinish() {
		Preferences.getInstance().getStore()
				.setValue(Preferences.USERID_KEY, firstPage.getUserId());
		return true;
	}

}
