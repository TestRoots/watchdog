package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationWizard;

/** Handler for displaying the user registration wizard. */
public class UserRegistrationWizardDialogHandler extends
		WizardDialogHandlerBase {

	/** Default constructor. */
	public UserRegistrationWizardDialogHandler() {
		super(new UserRegistrationWizard());
	}

}
