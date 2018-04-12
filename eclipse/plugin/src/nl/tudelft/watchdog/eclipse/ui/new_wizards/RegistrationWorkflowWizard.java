package nl.tudelft.watchdog.eclipse.ui.new_wizards;

import org.eclipse.jface.wizard.Wizard;

public class RegistrationWorkflowWizard extends Wizard {
	
	public RegistrationWorkflowWizard() {
		this.setHelpAvailable(false);
		this.setWindowTitle("WatchDog registration");
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void addPages() {
		this.addPage(new UserWelcomeScreen());
		this.addPage(new UserRegistrationPage());
	}

}
