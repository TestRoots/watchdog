package nl.tudelft.watchdog.eclipse.ui.handlers;

import nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration.ProjectRegistrationWizard;

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
