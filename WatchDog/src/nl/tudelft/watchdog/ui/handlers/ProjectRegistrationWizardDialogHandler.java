package nl.tudelft.watchdog.ui.handlers;

import nl.tudelft.watchdog.ui.wizards.projectregistration.ProjectRegistrationWizard;

/** Handler for displaying the project registration wizard. */
public class ProjectRegistrationWizardDialogHandler extends
		WizardDialogHandlerBase {

	/** Default constructor. */
	public ProjectRegistrationWizardDialogHandler() {
		super(new ProjectRegistrationWizard());
	}

}
