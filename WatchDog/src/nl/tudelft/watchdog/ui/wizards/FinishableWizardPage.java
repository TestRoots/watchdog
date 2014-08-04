package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.Activator;
import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.util.WatchDogGlobals;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * A {@link WizardPage} that can determine for itself via the
 * {@link #canFinish()} method, whether the wizard should be completeable via
 * the finish button.
 */
public abstract class FinishableWizardPage extends WizardPage {

	/** Constructor. */
	protected FinishableWizardPage(String pageName) {
		super(pageName);
		this.setImageDescriptor(WatchDogGlobals.tuLogoImageDescriptor);
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

	@Override
	public IWizardPage getPreviousPage() {
		if (canFinish()) {
			return null;
		}
		return super.getPreviousPage();
	}

	/**
	 * Creates a a new composite with the TestRoots and WatchDog logo.
	 */
	public void createLogoRow(Composite composite) {
		Composite logoContainer = UIUtils.createFullGridedComposite(composite,
				2);
		logoContainer
				.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label watchdogLogo = new Label(logoContainer, SWT.NONE);
		ImageDescriptor watchdogLogoImageDescriptor = Activator
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"resources/images/watchdog_small.png");
		watchdogLogo.setImage(watchdogLogoImageDescriptor.createImage());
		watchdogLogo.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING,
				true, false));

		Label testrootsLogo = new Label(logoContainer, SWT.NONE);
		ImageDescriptor testrootsImageDescriptor = Activator
				.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"resources/images/testroots_small.png");
		testrootsLogo.setImage(testrootsImageDescriptor.createImage());
		testrootsLogo.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING,
				true, false));
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
}
