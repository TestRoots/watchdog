package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Page that allows the user to enter how much time he spend on testing vs.
 * production code.
 *
 */
class ProjectRegistrationPage extends FinishableWizardPage {

	private Text projectNameInput;

	private Text userRoleInput;

	private Label multipelProjectLabel;

	private Composite noSingleProjectComposite;

	private Composite otherTestingStrategies;

	private Composite junitForTestingOnly;

	private Composite useJunit;

	/** Constructor. */
	protected ProjectRegistrationPage() {
		super("Register Project");
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Register a new project");
		setDescription("Create a new WatchDog Project for this workspace!");
		Composite topComposite = createComposite(parent);
		setControl(topComposite);
		setPageComplete(false);
	}

	/**
	 * Creates a slider so users can choose how much time they think they spend
	 * to testing vs. production code.
	 */
	private Composite createComposite(final Composite parent) {
		Composite topComposite = UIUtils.createFullGridedComposite(parent, 1);

		UIUtils.createBoldLabel("Register a new project", topComposite);

		Composite composite = UIUtils.createGridedComposite(topComposite, 2);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		noSingleProjectComposite = createSimpleYesNoQuestion(
				"Do the Eclipse projects in the workspace mainly belong to a single software project or program? ",
				composite);
		final Button noSingleProjectButton = (Button) noSingleProjectComposite
				.getChildren()[1];
		noSingleProjectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (noSingleProjectButton.getSelection()) {
					performUIUpdate(
							"Does at least one of your projects in the workspace ...",
							false);
				} else {
					performUIUpdate("Does your project ...", true);
				}
			}

			private void performUIUpdate(String label, boolean enableState) {
				multipelProjectLabel.setText(label);
				projectNameInput.setEnabled(enableState);
				parent.pack();
				parent.update();
				validateFormInputs();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Composite textInputComposite = UIUtils.createFullGridedComposite(
				topComposite, 2);
		projectNameInput = UIUtils
				.createLinkedFieldInput(
						"Project Name: ",
						"The name of the project(s) you work on in this workspace (if you have a website, we'd love to see it here)",
						textInputComposite);
		userRoleInput = UIUtils.createLinkedFieldInput("Your Role: ",
				"Try best describe what you do: Developer, Tester, ...",
				textInputComposite);
		FormValidationListener formValidationListener = new FormValidationListener(
				this);
		projectNameInput.addModifyListener(formValidationListener);
		userRoleInput.addModifyListener(formValidationListener);

		Composite questionComposite = UIUtils.createFullGridedComposite(
				topComposite, 2);

		multipelProjectLabel = UIUtils.createLabel(
				"Does your real-world project ...", questionComposite);
		UIUtils.createLabel("", questionComposite);
		useJunit = createSimpleYesNoDontKnowQuestion("  ... use JUnit? ",
				questionComposite);
		junitForTestingOnly = createSimpleYesNoDontKnowQuestion(
				"  ... use JUnit for unit testing only \n (i.e. only one class tested per test case)? ",
				questionComposite);
		otherTestingStrategies = createSimpleYesNoDontKnowQuestion(
				"  ... use other testing strategies, too \n (e.g. Mockito, Powermock, Selenium, or manual testing)? ",
				questionComposite);
		addValidationListenerToAllChildren(useJunit, formValidationListener);
		addValidationListenerToAllChildren(junitForTestingOnly,
				formValidationListener);
		addValidationListenerToAllChildren(otherTestingStrategies,
				formValidationListener);

		return topComposite;
	}

	private void addValidationListenerToAllChildren(Composite composite,
			FormValidationListener listener) {
		for (Control child : composite.getChildren()) {
			Button button = (Button) child;
			button.addSelectionListener(listener);
		}
	}

	/**
	 * Creates a simple question with according yes/no radio buttons.
	 * 
	 * @return the composite where the buttons are put onto.
	 */
	private Composite createSimpleYesNoQuestion(String question,
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
	private Composite createSimpleYesNoDontKnowQuestion(String question,
			Composite parent) {
		Composite buttonComposite = createSimpleYesNoQuestion(question, parent);
		UIUtils.createRadioButton(buttonComposite, "Don't know");
		return buttonComposite;
	}

	@Override
	public void validateFormInputs() {
		if (UIUtils.isEmptyOrWhitespaces(projectNameInput.getText())
				&& projectNameInput.getText().length() < 2
				&& projectNameInput.getEnabled()) {
			setErrorMessageAndPageComplete("You must enter a proper project's name.");
		} else if (userRoleInput.getText().isEmpty()) {
			setErrorMessageAndPageComplete("Please try to describe what you do in the project, e.g. developer or tester.");
		} else if (!hasOneSelection(noSingleProjectComposite)
				|| !hasOneSelection(useJunit)
				|| !hasOneSelection(junitForTestingOnly)
				|| !hasOneSelection(otherTestingStrategies)) {
			setErrorMessageAndPageComplete("Please answer all questions!");
		} else {
			setErrorMessageAndPageComplete(null);
		}
		getWizard().getContainer().updateButtons();
	}

	private boolean hasOneSelection(Composite composite) {
		boolean oneSelected = false;
		for (Control control : composite.getChildren()) {
			Button button = (Button) control;
			oneSelected = oneSelected ^ button.getSelection();
		}
		return oneSelected;
	}

	@Override
	public boolean canFinish() {
		if (getErrorMessage() == null) {
			return true;
		}
		return false;
	}

}
