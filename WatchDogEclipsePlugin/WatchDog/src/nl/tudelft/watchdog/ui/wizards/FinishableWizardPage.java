package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.core.ui.wizards.YesNoDontKnowChoice;
import nl.tudelft.watchdog.ui.util.UIUtils;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A {@link WizardPage} that can determine for itself via the
 * {@link #canFinish()} method, whether the wizard should be completeable via
 * the finish button.
 */
public abstract class FinishableWizardPage extends WizardPage {

	/** Number of the current page. */
	protected int currentPageNumber;

	/** Constructor. */
	protected FinishableWizardPage(String pageName, int pageNumber) {
		super(pageName);
		currentPageNumber = pageNumber;
		this.setImageDescriptor(UIUtils.TU_DELFT_LOGO);
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
	 * there is any. Empty default implementation can be overridden by
	 * subclasses.
	 */
	public void validateFormInputs() {
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

	@Override
	public IWizardPage getPreviousPage() {
		if (canFinish()) {
			return null;
		}
		return super.getPreviousPage();
	}

	/** Creates a a new composite with the TestRoots and WatchDog logo. */
	protected void createLogoRow(Composite composite) {
		Composite logoContainer = UIUtils.createGridedComposite(composite, 2);
		logoContainer
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		UIUtils.createWatchDogLogo(logoContainer);
		UIUtils.createLogo(logoContainer,
				"resources/images/testroots_small.png");
	}

	/**
	 * Given a composite consisting of three buttons Yes/No/Don't Know, returns
	 * which of the buttons was clicked.
	 */
	public YesNoDontKnowChoice evaluateWhichSelection(
			Composite yesNoDontKnowComposite) {
		if (((Button) yesNoDontKnowComposite.getChildren()[0]).getSelection()) {
			return YesNoDontKnowChoice.Yes;
		} else if (((Button) yesNoDontKnowComposite.getChildren()[1])
				.getSelection()) {
			return YesNoDontKnowChoice.No;
		}
		return YesNoDontKnowChoice.DontKnow;
	}

	/** Creates message on failure. */
	public static void createFailureMessage(Composite parent, String title,
			String message) {
		UIUtils.createBoldLabel(title, parent);
		Composite innerParent = UIUtils.createZeroMarginGridedComposite(parent,
				2);
		UIUtils.createLogo(innerParent, "resources/images/errormark.png");
		UIUtils.createLabel(message, innerParent);
	}

	/** Creates message on success. */
	public static void createSuccessMessage(Composite parent, String title,
			String message, String id) {
		UIUtils.createBoldLabel(title, parent);
		Composite innerParent = UIUtils.createZeroMarginGridedComposite(parent,
				2);
		UIUtils.createLogo(innerParent, "resources/images/checkmark.png");
		Composite displayInformation = UIUtils.createZeroMarginGridedComposite(
				innerParent, 2);
		UIUtils.createLabel(message, displayInformation);
		UIUtils.createTextField(displayInformation, id);
	}
}
