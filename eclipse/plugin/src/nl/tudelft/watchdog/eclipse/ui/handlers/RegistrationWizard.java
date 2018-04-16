package nl.tudelft.watchdog.eclipse.ui.handlers;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;

public interface RegistrationWizard extends IWizard {

	void setDialog(WizardDialog wizardDialog);
	
}
