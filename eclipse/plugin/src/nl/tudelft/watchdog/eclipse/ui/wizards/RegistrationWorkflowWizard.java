package nl.tudelft.watchdog.eclipse.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;

import nl.tudelft.watchdog.eclipse.Activator;
import nl.tudelft.watchdog.eclipse.ui.handlers.RegistrationWizard;
import nl.tudelft.watchdog.eclipse.ui.handlers.StartupHandler;

public class RegistrationWorkflowWizard extends Wizard implements RegistrationWizard {
	
	private static final String IMAGE_LOCATION = "resources/images/user.png";
	private WizardDialog dialog;

	public RegistrationWorkflowWizard() {
		this.setHelpAvailable(false);
		this.setWindowTitle("WatchDog registration");
		this.setDefaultPageImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, IMAGE_LOCATION));
	}

	@Override
	public boolean performFinish() {
		StartupHandler.startWatchDog();
		return true;
	}
	
	@Override
	public void addPages() {
		this.addPage(new UserWelcomeScreen());
		this.addPage(new UserRegistrationPage(this.dialog));
		this.addPage(new ProjectRegistrationPage(this.dialog));
	}

	@Override
	public void setDialog(WizardDialog wizardDialog) {
		this.dialog = wizardDialog;
	}
}
