package nl.tudelft.watchdog.eclipse.ui.handlers;

import nl.tudelft.watchdog.eclipse.ui.wizards.userregistration.UserProjectRegistrationWizard;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Handler for displaying the user registration wizard. */
public class UserRegistrationWizardDialogHandler extends
		WizardDialogHandlerBase {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return super.execute(new UserProjectRegistrationWizard(), event);
	}

}
