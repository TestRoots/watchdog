package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.exceptions.ServerCommunicationException;
import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.ui.wizards.Project;
import nl.tudelft.watchdog.ui.wizards.RegistrationEndingPage;

/**
 * Possible finishing page in the wizard. If the project exists on the server,
 * or the server is not reachable, the user can exit here.
 */
class ProjectCreatedEndingPage extends RegistrationEndingPage {

	@Override
	protected void makeRegistration() {
		Project project = new Project();

		ProjectSliderPage sliderPage;
		ProjectRegistrationPage projectPage = (ProjectRegistrationPage) getPreviousPage();
		if (getPreviousPage() instanceof ProjectSliderPage) {
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
		project.role = projectPage.userRoleInput.getText();
		project.usesJunit = projectPage.usesJunit();
		project.usesOtherFrameworks = projectPage.usesOtherTestingStrategies();

		windowTitle = "Project Registration";

		try {
			id = new JsonTransferer().registerNewProject(project);
		} catch (ServerCommunicationException exception) {
			successfulRegistration = false;
			messageTitle = "Problem creating new project!";
			messageBody = exception.getMessage();
			return;
		}

		successfulRegistration = true;
		((ProjectRegistrationWizard) getWizard()).projectId = id;
		messageTitle = "New project registered!";
		messageBody = "Your new project id "
				+ id
				+ " is registered.\nYou can change it and other WatchDog settings in the Eclipse preferences."
				+ ProjectIdEnteredEndingPage.encouragingEndMessage;
	}
}
