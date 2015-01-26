package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.wizards.RegistrationWizard;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectCreatedEndingPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectIdEnteredEndingPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectRegistrationPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectSliderPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectWelcomePage;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * A wizard that allows to register a new user or set an existing user. Does
 * some magic tricks to enable skipping of pages and recalculation of Finish
 * button.
 */
public class UserRegistrationWizard extends RegistrationWizard {

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
		userWelcomePage = new UserWelcomePage(1);
		addPage(userWelcomePage);
		userRegistrationPage = new UserRegistrationPage(2);
		addPage(userRegistrationPage);
		existingUserEndingPage = new UserIdEnteredEndingPage(2);
		addPage(existingUserEndingPage);
		projectWelcomePage = new ProjectWelcomePage(2);
		addPage(projectWelcomePage);
		existingProjectIdPage = new ProjectIdEnteredEndingPage(3);
		addPage(existingProjectIdPage);
		projectRegistrationPage = new ProjectRegistrationPage(3);
		addPage(projectRegistrationPage);
		projectSliderPage = new ProjectSliderPage(4);
		addPage(projectSliderPage);
		projectedCreatedPage = new ProjectCreatedEndingPage(5);
		addPage(projectedCreatedPage);
		this.totalPageNumber = 5;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == userWelcomePage
				&& !userWelcomePage.getRegisterNewId()) {
			return existingUserEndingPage;
		}
		if (currentPage == existingUserEndingPage) {
			return projectWelcomePage;
		}
		if (currentPage == userRegistrationPage) {
			return projectRegistrationPage;
		}
		if (currentPage == projectWelcomePage
				&& !projectWelcomePage.getRegisterNewId()) {
			return existingProjectIdPage;
		}
		if (currentPage == existingProjectIdPage) {
			return null;
		}
		if (currentPage == projectRegistrationPage
				&& projectRegistrationPage.shouldSkipProjectSliderPage()) {
			return projectedCreatedPage;
		}
		if (currentPage == projectSliderPage) {
			return projectedCreatedPage;
		}
		return super.getNextPage(page);
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == existingUserEndingPage
				&& !userWelcomePage.getRegisterNewId()) {
			return userWelcomePage;
		}
		if (currentPage == projectWelcomePage) {
			return existingUserEndingPage;
		}
		if (currentPage == existingProjectIdPage
				&& !projectWelcomePage.getRegisterNewId()) {
			return projectWelcomePage;
		}
		if (currentPage == projectRegistrationPage) {
			return projectWelcomePage.getRegisterNewId() ? projectWelcomePage
					: null;
		}
		return super.getPreviousPage(page);
	}

}
