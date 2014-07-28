package nl.tudelft.watchdog.ui.wizards;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * A universal listener that reacts on form modification events by reevaluating
 * the user inputs.
 */
public class FormValidationListener implements ModifyListener,
		SelectionListener {
	private FinishableWizardPage wizardPage;

	/** Constructor */
	public FormValidationListener(FinishableWizardPage wizardPage) {
		this.wizardPage = wizardPage;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		wizardPage.validateFormInputs();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		wizardPage.validateFormInputs();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}
}