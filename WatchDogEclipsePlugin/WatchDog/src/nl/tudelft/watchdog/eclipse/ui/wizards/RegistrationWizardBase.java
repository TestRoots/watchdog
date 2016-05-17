package nl.tudelft.watchdog.eclipse.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

import nl.tudelft.watchdog.eclipse.ui.handlers.StartupHandler;
import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectCreatedEndingPage;
import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectIdEnteredEndingPage;
import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectRegistrationPage;
import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectSliderPage;
import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectWelcomePage;

/** Base class for User and Project registration wizards. */
public abstract class RegistrationWizardBase extends Wizard {

	/**
	 * The project-id, either entered on the previous wizard pages or as
	 * retrieved by the server.
	 */
	protected String projectId;

	/** Maximum number of pages in the current wizard. */
	protected int totalPages;

	/** Page with Yes/No question about registering new project ID. */
	public ProjectWelcomePage projectWelcomePage;

	/** Page after existing project ID has been entered. */
	protected ProjectIdEnteredEndingPage existingProjectIdPage;

	/** First page of project registration. */
	protected ProjectRegistrationPage projectRegistrationPage;

	/** Second part of project registration. */
	protected ProjectSliderPage projectSliderPage;

	/** Project registration completed. */
	protected ProjectCreatedEndingPage projectCreatedPage;

	@Override
	public boolean performFinish() {
		StartupHandler.startWatchDog();
		return true;
	}

	@Override
	public boolean canFinish() {
		FinishableWizardPage currentPage = (FinishableWizardPage) getContainer()
				.getCurrentPage();
		return currentPage.canFinish();
	}

	/** Returns project-ID. */
	public String getProjectId() {
		return projectId;
	}

	/** Sets projectId. */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/** Returns total number of pages in current wizard. */
	public int getTotalPages() {
		return totalPages;
	}

	/** (Sets total number of pages in current wizard. */
	public void setTotalPageNumber(int totalPages) {
		this.totalPages = totalPages;
	}

}
