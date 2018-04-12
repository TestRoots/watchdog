package nl.tudelft.watchdog.eclipse.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

import nl.tudelft.watchdog.core.ui.wizards.RegistrationWizard;

public class EclipseUserProjectRegistrationWizard extends Wizard implements RegistrationWizard {

	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public void addPages() {
		this.addPage(new EclipseUserWelcomePage());
		this.addPage(new EclipseUserRegistrationPage(this));
	}

	@Override
	public void updateButtons() {
		this.getContainer().updateButtons();
		this.getShell().layout(true, true);
		this.getShell().redraw();
		this.getShell().update();
	}
}
