package nl.tudelft.watchdog.eclipse.ui.wizards.userregistration;

import org.eclipse.jface.wizard.IWizardPage;

import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectCreatedEndingPage;

/**
 * A wizard that allows to register a new user or set an existing user, and then
 * continues with project registration.
 */
public class UserProjectRegistrationWizard extends RegistrationWizardBase {

	/** The first page in the wizard. */
	public UserWelcomePage userWelcomePage;

	/** The page with all the actual user info (name, email, etc.) on it. */
	public UserRegistrationPage userRegistrationPage;

	/** When a user already exists ... */
	/* package */UserIdEnteredEndingPage existingUserEndingPage;

	/**
	 * The userid, either entered on this page or as retrieved by the server.
	 */
	/* package */String userid;

	@Override
	public void addPages() {
		userWelcomePage = new UserWelcomePage();
		addPage(userWelcomePage);
		projectCreatedPage = new ProjectCreatedEndingPage(2);
		addPage(projectCreatedPage);
		this.totalPages = 2;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == userWelcomePage) {
			return projectCreatedPage;
		}
		return super.getNextPage(page);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == projectCreatedPage) {
			// Disable going back.
			return null;
		}
		return super.getPreviousPage(page);
	}

}
