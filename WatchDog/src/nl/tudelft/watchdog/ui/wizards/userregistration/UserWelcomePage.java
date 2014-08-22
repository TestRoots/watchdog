package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.wizards.WelcomePage;

/**
 * The first page of the {@link UserRegistrationWizard}. It asks the question:
 * Are you a new WatchDog user, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
class UserWelcomePage extends WelcomePage {

	/** Constructor. */
	UserWelcomePage() {
		super("Welcome to WatchDog!");
		setDescription("This wizard guides you through the setup of a WatchDog User.");
		welcomeTitle = "Welcome! Registering a new user takes under 1 minute!";
		welcomeText = "";
		labelText = "Your WatchDog User-ID: ";
		inputToolTip = "The User-ID we sent you upon your first WatchDog registration.";
		labelQuestion = "Do you have a WatchDog UserId? ";
	}

	@Override
	protected String getIconPath() {
		return "resources/images/user.png";
	}
}