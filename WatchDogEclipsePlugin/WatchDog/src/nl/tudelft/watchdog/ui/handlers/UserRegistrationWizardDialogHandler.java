package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.ui.wizards.userregistration.UserRegistrationWizard;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Handler for displaying the user registration wizard. */
public class UserRegistrationWizardDialogHandler extends
		WizardDialogHandlerBase {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return super.execute(new UserRegistrationWizard(), event);
	}

}
