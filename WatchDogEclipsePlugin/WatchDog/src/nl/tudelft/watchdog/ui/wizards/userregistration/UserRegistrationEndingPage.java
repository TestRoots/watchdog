package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.logic.network.JsonTransferer;
import nl.tudelft.watchdog.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.ui.preferences.Preferences;
import nl.tudelft.watchdog.ui.wizards.RegistrationEndingPage;
import nl.tudelft.watchdog.ui.wizards.User;

import org.eclipse.core.runtime.Platform;

/** Page called when registering a new user with the server. */
class UserRegistrationEndingPage extends RegistrationEndingPage {

	protected void makeRegistration() {
		UserRegistrationPage page = ((UserRegistrationWizard) getWizard()).userRegistrationPage;
		User user = new User();
		user.email = page.getEmailInput().getText();
		user.organization = page.getOrganizationInput().getText();
		user.group = page.getGroupInput().getText();
		user.mayContactUser = page.getMayContactUser();
		user.programmingExperience = page.getProgrammingExperience();
		user.operatingSystem = Platform.getOS();

		windowTitle = "User Registration";

		try {
			id = new JsonTransferer().registerNewUser(user);
		} catch (ServerCommunicationException exception) {
			successfulRegistration = false;
			messageTitle = "Problem creating new user!";
			messageBody = exception.getMessage();
			return;
		}

		successfulRegistration = true;
		((UserRegistrationWizard) getWizard()).userid = id;
		messageTitle = "New user registered!";
		messageBody = "Your new user id "
				+ id
				+ " is registered.\nYou can change it and other WatchDog settings in the Eclipse preferences."
				+ UserIdEnteredEndingPage.ENCOURAGING_END_MESSAGE;

		Preferences.getInstance().setUserid(id);
	}

	@Override
	public boolean canFinish() {
		return false;
	}
}
