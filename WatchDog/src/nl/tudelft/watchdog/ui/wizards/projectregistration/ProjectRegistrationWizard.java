package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/** Wizard showing the project registration for WatchDog. */
public class ProjectRegistrationWizard extends Wizard {

	private ProjectWelcomePage welcomePage;
	private IWizardPage existingProjectIdPage;
	private ProjectSliderPage projectSliderPage;
	private ProjectCreatedEndingPage projectedCreatedPage;
	private ProjectRegistrationPage projectRegistrationPage;

	@Override
	public void addPages() {
		welcomePage = new ProjectWelcomePage();
		addPage(welcomePage);
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
	public boolean canFinish() {
		FinishableWizardPage currentPage = (FinishableWizardPage) getContainer()
				.getCurrentPage();
		return currentPage.canFinish();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage currentPage = getContainer().getCurrentPage();
		if (currentPage == welcomePage && !welcomePage.getRegisterNewId()) {
			return existingProjectIdPage;
		}
		if (currentPage == existingProjectIdPage) {
			return null;
		}
		if (currentPage == projectRegistrationPage
				&& !projectRegistrationPage.usesOtherTestingStrategies()
				&& !projectRegistrationPage.usesJunit()) {
			return projectedCreatedPage;
		}
		if (currentPage == projectSliderPage) {
			return projectedCreatedPage;
		}
		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}
}
