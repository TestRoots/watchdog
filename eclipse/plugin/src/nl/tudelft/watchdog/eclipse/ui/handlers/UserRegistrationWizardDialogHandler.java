package nl.tudelft.watchdog.eclipse.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationWorkflowWizard;

public class UserRegistrationWizardDialogHandler extends WizardDialogHandlerBase {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return super.execute(new RegistrationWorkflowWizard(), event);
	}
}