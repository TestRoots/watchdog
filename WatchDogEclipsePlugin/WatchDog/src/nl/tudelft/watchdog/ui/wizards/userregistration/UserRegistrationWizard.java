package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectCreatedEndingPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectIdEnteredEndingPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectRegistrationPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectSliderPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectWelcomePage;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * A wizard that allows to register a new user or set an existing user. Does
 * some magic tricks to enable skipping of pages and recalculation of Finish
 * button.
 */
public class UserRegistrationWizard extends Wizard {

	/** The first page in the wizard. */
	/* package */UserWelcomePage welcomePage;

	/** When a user already exists ... */
	/* package */UserIdEnteredEndingPage existingUserEndingPage;

	/** The page with all the actual user info (name, email, etc.) on it. */
	public UserRegistrationPage userRegistrationPage;

	/**
	 * The userid, either entered on this page or as retrieved by the server.
	 */
	/* package */String userid;

	/**
	 * The projectid, either entered on the previous wizard pages or as
	 * retrieved by the server.
	 */
	public String projectId;

	private ProjectWelcomePage welcomeProjectPage;
	private IWizardPage existingProjectIdPage;
	private ProjectSliderPage projectSliderPage;
	private ProjectCreatedEndingPage projectedCreatedPage;
	private ProjectRegistrationPage projectRegistrationPage;

	@Override
	public void addPages() {
		welcomePage = new UserWelcomePage();
		addPage(welcomePage);
		addPage(new UserWatchDogDescriptionPage());
		userRegistrationPage = new UserRegistrationPage();
		addPage(userRegistrationPage);
		existingUserEndingPage = new UserIdEnteredEndingPage();
		addPage(existingUserEndingPage);
		welcomeProjectPage = new ProjectWelcomePage();
		addPage(welcomeProjectPage);
		projectRegistrationPage = new ProjectRegistrationPage();
		addPage(projectRegistrationPage);
		projectSliderPage = new ProjectSliderPage();
		addPage(projectSliderPage);
		existingProjectIdPage = new ProjectIdEnteredEndingPage();
		addPage(existingProjectIdPage);
		projectedCreatedPage = new ProjectCreatedEndingPage();
		addPage(projectedCreatedPage);
	}

	@Override
	public boolean performFinish() {
		Preferences.getInstance().registerWorkspaceProject(
				UIUtils.getWorkspaceName(), projectId);
		return true;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == welcomePage && !welcomePage.getRegisterNewId()) {
			return existingUserEndingPage;
		}
		if (currentPage == existingUserEndingPage) {
			return welcomeProjectPage;
		}
		if (currentPage == userRegistrationPage) {
			return welcomeProjectPage;
		}
		if (currentPage == welcomeProjectPage
				&& !welcomeProjectPage.getRegisterNewId()) {
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
				&& !welcomePage.getRegisterNewId()) {
			return welcomePage;
		}
		if (currentPage == welcomeProjectPage) {
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
