package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.ui.handlers.StartUpHandler;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectCreatedEndingPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectIdEnteredEndingPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectRegistrationPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectSliderPage;
import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectWelcomePage;

import org.eclipse.jface.wizard.Wizard;

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
	protected ProjectCreatedEndingPage projectedCreatedPage;

	@Override
	public boolean performFinish() {
		Preferences preferences = Preferences.getInstance();
		preferences.registerWorkspaceProject(UIUtils.getWorkspaceName(),
				projectId);
		preferences.registerWorkspaceUse(UIUtils.getWorkspaceName(), true);
		StartUpHandler.startWatchDog();
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
	public int getTotalPageNumber() {
		return totalPages;
	}

	/** (Re)Sets total number of pages in current wizard. */
	public void setTotalPageNumber(int totalPageNumber) {
		this.totalPages = totalPageNumber;
	}

}
