package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.wizards.WelcomePage;
import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationWizard;

/**
 * The first page of the {@link UserRegistrationWizard}. It asks the question:
 * Are you a new WatchDog user, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
class ProjectWelcomePage extends WelcomePage {

	/** Constructor. */
	ProjectWelcomePage() {
		super("Welcome to WatchDog Project Registration!");
		setDescription("This wizard will guide you through the setup of a WatchDog Project. May we ask for one minute of your time?");
		labelQuestion = "Did you already register the project you are working on in this workspace? ";
		welcomeTitle = "Thanks for registering a new project!";
		welcomeText = "A WatchDog Project is a software development product or project. It only transfers the concept to the Eclipse world.\n\nWe assume that one Eclipse workspace contains one real-world project (usually). In other words, all Eclipse projects in your current workspace form a WatchDog project (or, ideally should do that).\n\nIt is ideal if you use separate workspaces for different projects. WatchDog will also work if you do not. Only our data will be not as fine-grained and useful for research. So consider doing us a favour.";
		labelText = "Your WatchDog Project ID: ";
		inputToolTip = "The Project ID we sent you upon your Project registration.";
	}

}