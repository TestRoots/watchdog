package nl.tudelft.watchdog.ui.newUserWizard;

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
		// addPage(new UserRegistrationPage());
		// existingUserEndingPage = new ExistingUserEndingPage();
		// addPage(existingUserEndingPage);
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == existingUserEndingPage
				&& existingUserEndingPage.canFinish()) {
			return true;
		}
		return super.canFinish();
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
