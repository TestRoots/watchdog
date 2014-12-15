package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectRegistrationWizard;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/** Handler for displaying the project registration wizard. */
public class ProjectRegistrationWizardDialogHandler extends
		WizardDialogHandlerBase {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return super.execute(new ProjectRegistrationWizard(), event);
	}

}
