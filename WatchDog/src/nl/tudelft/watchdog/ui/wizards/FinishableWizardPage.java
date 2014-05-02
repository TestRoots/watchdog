package nl.tudelft.watchdog.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;

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
	abstract boolean canFinish();

	/**
	 * Calls {@link #setErrorMessage(String)} with the supplied errorMessage,
	 * and {@link #setPageComplete(boolean)} true if the message is empty, and
	 * false otherwise.
	 */
	void setErrorMessageAndPageComplete(String errorMessage) {
		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null ? true : false);
	}
}
