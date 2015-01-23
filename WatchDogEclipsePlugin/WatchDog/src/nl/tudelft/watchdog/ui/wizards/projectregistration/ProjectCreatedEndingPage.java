package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.Project;
import nl.tudelft.watchdog.ui.wizards.RegistrationEndingPage;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationPage;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationWizard;
import nl.tudelft.watchdog.util.WatchDogLogger;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the project exists on the server,
 * or the server is not reachable, the user can exit here.
 */
public class ProjectCreatedEndingPage extends RegistrationEndingPage {

	/** The top-level composite. */
	private Composite topComposite;

	private Composite dynamicComposite;

	private String concludingMessage;

	/** Constructor. */
	public ProjectCreatedEndingPage() {
		super("Project-ID created.");
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
		if (isThisProjectWizzard()) {
			((ProjectRegistrationWizard) getWizard()).projectId = id;
		} else {
			((UserRegistrationWizard) getWizard()).projectId = id;
		}

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
		if (isThisProjectWizzard()) {
			dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
			dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());
			createProjectRegistrationSummary();
			return;
		} else {
			UserRegistrationPage userRegistrationPage = ((UserRegistrationWizard) getWizard()).userRegistrationPage;
			dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
			dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());
			userRegistrationPage
					.createUserRegistrationSummary(dynamicComposite);
			createProjectRegistrationSummary();
			return;
		}
	}

	private void createProjectRegistrationSummary() {
		UIUtils.createBoldLabel(messageTitle, dynamicComposite);
		setTitle(windowTitle);
		Composite innerParent = UIUtils.createZeroMarginGridedComposite(
				dynamicComposite, 2);
		if (successfulRegistration) {
			UIUtils.createLogo(innerParent, "resources/images/checkmark.png");
			Composite displayInformation = UIUtils
					.createZeroMarginGridedComposite(innerParent, 2);
			UIUtils.createLabel(messageBody, displayInformation);
			UIUtils.createTextField(displayInformation, id);
			UIUtils.createLabel(concludingMessage, dynamicComposite);
		} else {
			UIUtils.createLogo(innerParent, "resources/images/error.png");
			UIUtils.createLabel(messageBody, innerParent);
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
