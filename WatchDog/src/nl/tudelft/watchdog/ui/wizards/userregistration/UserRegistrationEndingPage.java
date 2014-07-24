package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.logic.ServerCommunicationException;
import nl.tudelft.watchdog.logic.User;
import nl.tudelft.watchdog.logic.interval.JsonTransferer;
import nl.tudelft.watchdog.ui.wizards.RegistrationEndingPage;

/**
 * @author mbeller
 *
 */
class UserRegistrationEndingPage extends RegistrationEndingPage {
	protected void makeRegistration() {
		UserRegistrationPage page = ((UserRegistrationWizard) getWizard()).userRegistrationPage;
		User user = new User();
		user.email = page.getEmailInput().getText();
		user.organization = page.getOrganizationInput().getText();
		user.group = page.getGroupInput().getText();
		user.mayContactUser = page.getMayContactUser();

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
				+ " is registered.\nIf you ever have to, you can change other WatchDog settings in the Eclipse preferences."
				+ UserIdEnteredEndingPage.encouragingEndMessage;
	}
}
