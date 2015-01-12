package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.wizards.WelcomePage;

/**
 * The first page of the {@link ProjectRegistrationWizard}. It asks the
 * question: do you have a Project ID, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
class ProjectWelcomePage extends WelcomePage {

	/** Constructor. */
	ProjectWelcomePage() {
		super("Welcome to WatchDog Project Registration!");
		setDescription("This wizard guides you through the setup of a WatchDog Project. ");
		labelQuestion = "Have you got a WatchDog Project-ID for this workspace? ";
		welcomeTitle = "Registering a new project takes just 1 minute, and you are done. ";
		welcomeText = "";
		labelText = "Your WatchDog Project ID: ";
		inputToolTip = "The Project ID we sent you upon your Project registration.";
	}

	@Override
	protected String getIconPath() {
		return "resources/images/project.png";
	}
}