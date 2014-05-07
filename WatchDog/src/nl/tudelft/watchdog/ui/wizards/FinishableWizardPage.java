package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A {@link WizardPage} that can determine for itself via the
 * {@link #canFinish()} method, whether the wizard should be completeable via
 * the finish button.
 */
public abstract class FinishableWizardPage extends WizardPage {

	/** Constructor. */
	protected FinishableWizardPage(String pageName) {
		super(pageName);
	}

	/** @return whether this page can currently be finished. */
	public abstract boolean canFinish();

	/**
	 * Calls {@link #setErrorMessage(String)} with the supplied errorMessage,
	 * and {@link #setPageComplete(boolean)} true if the message is empty, and
	 * false otherwise.
	 */
	protected void setErrorMessageAndPageComplete(String errorMessage) {
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null ? true : false);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		getWizard().getContainer().updateButtons();
	}

	/**
	 * Validates the form inputs, and sets the error message for the wizard if
	 * there is any. Should be overriden by subclasses.
	 */
	public void validateFormInputs() {
	}

	/**
	 * Creates a simple question with according yes/no radio buttons.
	 * 
	 * @return the composite where the buttons are put onto.
	 */
	protected Composite createSimpleYesNoQuestion(String question,
			Composite parent) {
		UIUtils.createLabel(question, parent);
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayout(new FillLayout());
		UIUtils.createRadioButton(composite, "Yes");
		UIUtils.createRadioButton(composite, "No");
		return composite;
	}

	/**
	 * Creates a simple question with according yes/no/don't know radio buttons.
	 * 
	 * @return the composite where the buttons are put onto.
	 */
	protected Composite createSimpleYesNoDontKnowQuestion(String question,
			Composite parent) {
		Composite buttonComposite = createSimpleYesNoQuestion(question, parent);
		UIUtils.createRadioButton(buttonComposite, "Don't know");
		return buttonComposite;
	}

	/** Use on composites which consist of radio buttons only. */
	protected void addValidationListenerToAllChildren(Composite composite,
			FormValidationListener listener) {
		for (Control child : composite.getChildren()) {
			Button button = (Button) child;
			button.addSelectionListener(listener);
		}
	}

	/**
	 * Checks for the given composite if of all its children, which are assumed
	 * to be Buttons, at least one has been selected. Use on composites which
	 * consist of radio buttons only.
	 */
	protected boolean hasOneSelection(Composite composite) {
		boolean oneSelected = false;
		for (Control control : composite.getChildren()) {
			Button button = (Button) control;
			oneSelected = oneSelected ^ button.getSelection();
		}
		return oneSelected;
	}
}
