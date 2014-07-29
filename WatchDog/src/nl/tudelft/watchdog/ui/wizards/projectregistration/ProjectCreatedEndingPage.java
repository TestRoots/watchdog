package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.logic.Project;
import nl.tudelft.watchdog.logic.exceptions.ServerCommunicationException;
import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.ui.wizards.RegistrationEndingPage;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
class ProjectCreatedEndingPage extends RegistrationEndingPage {

	@Override
	protected void makeRegistration() {
		ProjectSliderPage sliderPage = (ProjectSliderPage) getPreviousPage();
		ProjectRegistrationPage projectPage = (ProjectRegistrationPage) getPreviousPage()
				.getPreviousPage();
		Project project = new Project();

		// initialize from projectPage
		project.belongToASingleSofware = !projectPage.noSingleProjectButton
				.getSelection();
		project.name = projectPage.projectNameInput.getText();
		project.role = projectPage.userRoleInput.getText();
		project.usesJunit = projectPage.usesJunit();
		project.usesOtherFrameworks = projectPage.usesOtherTestingStrategies();

		project.productionPercentage = sliderPage.percentageProductionSlider
				.getSelection();
		project.useJunitOnlyForUnitTesting = sliderPage
				.usesJunitForUnitTestingOnly();
		project.followTestDrivenDesign = sliderPage.usesTestDrivenDesing();

		windowTitle = "User Registration";

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
		messageTitle = "New user registered!";
		messageBody = "Your new user id "
				+ id
				+ " is registered.\nIf you ever have to, you can change other WatchDog settings in the Eclipse preferences."
				+ ProjectIdEnteredEndingPage.encouragingEndMessage;
	}
}
