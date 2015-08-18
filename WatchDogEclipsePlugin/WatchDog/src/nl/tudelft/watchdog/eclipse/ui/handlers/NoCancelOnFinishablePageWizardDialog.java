package nl.tudelft.watchdog.eclipse.ui.handlers;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/** A special WizardDialog that makes it impossible to cancel on a last page. */
public class NoCancelOnFinishablePageWizardDialog extends WizardDialog {

	/** Constructor. */
	public NoCancelOnFinishablePageWizardDialog(Shell parentShell,
			IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	public void updateButtons() {
		super.updateButtons();
		if (getCurrentPage().isPageComplete()) {
			getButton(CANCEL).setEnabled(false);
		} else {
			getButton(CANCEL).setEnabled(true);
		}
	}

}
