package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ui.DocumentAdapter;

import javax.swing.event.DocumentEvent;
import java.awt.event.*;

/**
 * A universal listener that reacts on form modification events by reevaluating
 * the user inputs.
 */
public class FormValidationListener extends DocumentAdapter implements ItemListener {
	private WizardStep wizardStep;

	/** Constructor */
	public FormValidationListener(WizardStep wizardStep) {
		this.wizardStep = wizardStep;
	}

    @Override
    public void itemStateChanged(ItemEvent e) {
        wizardStep.validateFormInputs();
    }

    @Override
    protected void textChanged(DocumentEvent e) {
        wizardStep.validateFormInputs();
    }
}
