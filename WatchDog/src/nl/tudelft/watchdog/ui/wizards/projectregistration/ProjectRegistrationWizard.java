package nl.tudelft.watchdog.ui.wizards.projectregistration;

import org.eclipse.jface.wizard.Wizard;

/** Wizard showing the project registration for WatchDog. */
public class ProjectRegistrationWizard extends Wizard {

	@Override
	public void addPages() {
		addPage(new ProjectWelcomePage());
		addPage(new ProjectRegistrationPage());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
