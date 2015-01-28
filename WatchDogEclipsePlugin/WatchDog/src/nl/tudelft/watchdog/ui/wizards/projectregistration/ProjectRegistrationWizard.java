package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;

import org.eclipse.jface.wizard.IWizardPage;

/** Wizard showing the project registration for WatchDog. */
public class ProjectRegistrationWizard extends RegistrationWizardBase {

	@Override
	public void addPages() {
		projectWelcomePage = new ProjectWelcomePage(1);
		addPage(projectWelcomePage);
		projectRegistrationPage = new ProjectRegistrationPage(2);
		addPage(projectRegistrationPage);
		projectSliderPage = new ProjectSliderPage(3);
		addPage(projectSliderPage);
		existingProjectIdPage = new ProjectIdEnteredEndingPage(2);
		addPage(existingProjectIdPage);
		projectedCreatedPage = new ProjectCreatedEndingPage(4);
		addPage(projectedCreatedPage);
		this.totalPages = 4;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
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
}
