package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.Project;
import nl.tudelft.watchdog.ui.wizards.RegistrationEndingPageBase;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationPage;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationWizard;
import nl.tudelft.watchdog.util.WatchDogLogger;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the project exists on the server,
 * or the server is not reachable, the user can exit here.
 */
public class ProjectCreatedEndingPage extends RegistrationEndingPageBase {

	/** The top-level composite. */
	private Composite topComposite;

	private Composite dynamicComposite;

	private String concludingMessage;

	/** Constructor. */
	public ProjectCreatedEndingPage(int pageNumber) {
		super("Project-ID created.", pageNumber);
		concludingMessage = "You can change these and other WatchDog settings in the Eclipse preferences."
				+ ProjectIdEnteredEndingPage.ENCOURAGING_END_MESSAGE;
	}

	@Override
	protected void makeRegistration() {
		Project project = new Project(Preferences.getInstance().getUserid());

		ProjectSliderPage sliderPage;
		ProjectRegistrationPage projectPage = null;
		if (getPreviousPage() instanceof ProjectRegistrationPage) {
			projectPage = (ProjectRegistrationPage) getPreviousPage();
		} else if (getPreviousPage() instanceof ProjectSliderPage) {
			sliderPage = (ProjectSliderPage) getPreviousPage();
			projectPage = (ProjectRegistrationPage) getPreviousPage()
					.getPreviousPage();

			project.productionPercentage = sliderPage.percentageProductionSlider
					.getSelection();
			project.useJunitOnlyForUnitTesting = sliderPage
					.usesJunitForUnitTestingOnly();
			project.followTestDrivenDesign = sliderPage.usesTestDrivenDesing();
		}

		// initialize from projectPage
		project.belongToASingleSofware = !projectPage.noSingleProjectButton
				.getSelection();
		project.name = projectPage.projectNameInput.getText();
		project.website = projectPage.projectWebsite.getText();
		project.usesJunit = projectPage.usesJunit();
		project.usesOtherTestingFrameworks = projectPage
				.usesOtherTestingFrameworks();
		project.usesOtherTestingForms = projectPage.usesOtherTestingForms();

		windowTitle = "Registration Summary";

		try {
			id = new JsonTransferer().registerNewProject(project);
		} catch (ServerCommunicationException exception) {
			successfulRegistration = false;
			messageTitle = "Problem creating new project!";
			messageBody = exception.getMessage();
			messageBody += "\nAre you connected to the internet, and is port 80 open?";
			messageBody += "\nPlease contact us via www.testroots.org. \nWe'll troubleshoot the issue!";
			WatchDogLogger.getInstance().logSevere(exception);
			return;
		}

		successfulRegistration = true;

		((RegistrationWizardBase) getWizard()).setProjectId(id);

		messageTitle = "New project registered!";
		messageBody = "Your new project id is registered: ";
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (dynamicComposite != null) {
				dynamicComposite.dispose();
			}
			makeRegistration();
			createPageContent();
			dynamicComposite.layout(true);
			topComposite.layout(true);
		}
	}

	private void createPageContent() {
		setTitle(windowTitle);
		if (isThisProjectWizzard()) {
			dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
			dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());
			createProjectRegistrationSummary();
			return;
		} else {
			UserRegistrationWizard wizard = (UserRegistrationWizard) getWizard();
			UserRegistrationPage userRegistrationPage = wizard.userRegistrationPage;
			dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
			dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());
			if (wizard.userWelcomePage.getRegisterNewId()) {
				userRegistrationPage
						.createUserRegistrationSummary(dynamicComposite);
			}
			createProjectRegistrationSummary();
			return;
		}
	}

	private void createProjectRegistrationSummary() {
		if (successfulRegistration) {
			UIUtils.createSuccessMessage(dynamicComposite, messageTitle,
					messageBody, id);
			UIUtils.createLabel(concludingMessage, dynamicComposite);
		} else {
			UIUtils.createFailureMessage(dynamicComposite, messageTitle,
					messageBody);
			setPageComplete(false);
		}
	}

	private boolean isThisProjectWizzard() {
		IWizard wizard = getWizard();
		return wizard instanceof ProjectRegistrationWizard;
	}

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("", topComposite);
		setControl(topComposite);
	}
}
