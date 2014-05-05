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
		super("Welcome to WatchDog!");
		setDescription("This wizard will guide you through the setup of a WatchDog User. May we ask for one minute of your time?");
		welcomeTitle = "Welcome, new WatchDog User!";
		welcomeText = "WatchDog keeps track of the way you develop and test your software. Your usage data is sent to and maintained by the TestRoots team at Delft University. We are never going to do anything bad with it.\n\nYou can stay completely anonymous. But our research greatly improves, if you provide us with a bit of info about you. This way, you can also win one of our amazing prices.\n\nIf you want to know more about WatchDog (or the prices to win), visit our website <a href=\"http://watchdog.testroots.org\">watchdog.testroots.org</a>.";
		labelText = "Your WatchDog User ID: ";
		inputToolTip = "The User ID we sent you upon your first WatchDog registration.";
	}

}