package nl.tudelft.watchdog.ui.newUserWizard;

import org.eclipse.jface.wizard.Wizard;

/** A wizard that allows to register a new user or set an existing user. */
public class NewUserWizard extends Wizard {

	@Override
	public void addPages() {
		addPage(new FirstPage());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

}
