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
		setDescription("This wizard will guide you through the setup of a WatchDog Project. May we ask for one minute of your time?");
		labelQuestion = "Did you already register the project you are working on in this workspace? ";
		welcomeTitle = "Thanks for registering a new project!";
		welcomeText = "A WatchDog Project can be any software development product or project. It only transfers the concept to the Eclipse world.\n\nUsually, one Eclipse workspace contains one real-world project. So, all Eclipse projects in your current workspace form a WatchDog project (or, ideally should do that).\n\nIf they do not do that, you can register a 'mixed' project. But it is ideal if you use separate workspaces for different projects. Only our data will be not as fine-grained and useful for research. So consider doing us a favour.";
		labelText = "Your WatchDog Project ID: ";
		inputToolTip = "The Project ID we sent you upon your Project registration.";
	}

}