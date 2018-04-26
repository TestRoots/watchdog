package nl.tudelft.watchdog.eclipse.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.ui.handlers.RegistrationWizard;
import nl.tudelft.watchdog.eclipse.ui.handlers.StartupHandler;

/**
 * Wizard that asks users for registering a newly imported project with WatchDog.
 */
public class NewProjectWizard extends Wizard implements RegistrationWizard {

	private static final String IMAGE_LOCATION = "resources/images/project.png";
	private WizardDialog dialog;

	public NewProjectWizard() {
		this.setHelpAvailable(false);
		this.setWindowTitle("Project registration");
		this.setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, IMAGE_LOCATION));
	}

	@Override
	public boolean performFinish() {
		StartupHandler.startWatchDog();
		return true;
	}
	
	@Override
	public void addPages() {
		this.addPage(new ProjectRegistrationPage(this.dialog));
	}

	@Override
	public void setDialog(WizardDialog wizardDialog) {
		this.dialog = wizardDialog;
	}

}
