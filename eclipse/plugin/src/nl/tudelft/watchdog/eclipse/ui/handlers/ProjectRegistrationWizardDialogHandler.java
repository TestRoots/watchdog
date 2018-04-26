package nl.tudelft.watchdog.eclipse.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import nl.tudelft.watchdog.eclipse.ui.wizards.NewProjectWizard;

/**
 * Handler for displaying the project registration wizard.
 */
public class ProjectRegistrationWizardDialogHandler extends WizardDialogHandlerBase {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return super.execute(new NewProjectWizard(), event);
	}

}
