package nl.tudelft.watchdog.eclipse.ui.handlers;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;

/**
 * Marker interface for a Wizard to be able to handle a particular dialog.
 * Used to correctly update buttons called on {@link WizardDialog#updateButtons()}.
 */
public interface RegistrationWizard extends IWizard {

	void setDialog(WizardDialog wizardDialog);

}
