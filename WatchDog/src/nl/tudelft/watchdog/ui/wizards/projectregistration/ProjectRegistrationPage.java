package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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

	private Composite useJunit;

	private Composite otherTestingStrategies;

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

		UIUtils.createLabel("", questionComposite);
		UIUtils.createLabel("", questionComposite);
		multipelProjectLabel = UIUtils.createLabel(
				"Does your real-world project ...", questionComposite);
		UIUtils.createLabel("", questionComposite);
		useJunit = createSimpleYesNoDontKnowQuestion("  ... use JUnit? \n",
				questionComposite);
		otherTestingStrategies = createSimpleYesNoDontKnowQuestion(
				"  ... use other testing 'frameworks' than JUnit, for example \n  Mockito, Powermock, Selenium, or manual testing? ",
				questionComposite);
		addValidationListenerToAllChildren(useJunit, formValidationListener);
		addValidationListenerToAllChildren(otherTestingStrategies,
				formValidationListener);

		return topComposite;
	}

	@Override
	public void validateFormInputs() {
		if (!hasOneSelection(noSingleProjectComposite)) {
			setErrorMessageAndPageComplete("Please answer all yes/no questions!");
		} else if (inputFieldDoesNotHaveMinimumSensibleInput(projectNameInput)
				&& projectNameInput.getEnabled()) {
			setErrorMessageAndPageComplete("You must enter a proper project's name.");
		} else if (inputFieldDoesNotHaveMinimumSensibleInput(userRoleInput)) {
			setErrorMessageAndPageComplete("Please try to describe what you do in the project, e.g. developer or tester.");
		} else if (!hasOneSelection(useJunit)
				|| !hasOneSelection(otherTestingStrategies)) {
			setErrorMessageAndPageComplete("Please answer all yes/no/don't know questions!");
		} else {
			setErrorMessageAndPageComplete(null);
		}
		getWizard().getContainer().updateButtons();
	}

	private boolean inputFieldDoesNotHaveMinimumSensibleInput(Text input) {
		return UIUtils.isEmptyOrHasOnlyWhitespaces(input.getText())
				|| input.getText().length() < 3;
	}

	@Override
	public boolean canFinish() {
		return false;
	}

	/**
	 * @return <code>true</code> if this project uses Junit. <code>false</code>
	 *         otherwise.
	 */
	boolean usesJunit() {
		return ((Button) useJunit.getChildren()[0]).getSelection();
	}

	/**
	 * @return <code>true</code> if this project uses other testing strategies
	 *         than Junit. <code>false</code> otherwise.
	 */
	boolean usesOtherTestingStrategies() {
		return ((Button) otherTestingStrategies.getChildren()[0])
				.getSelection();
	}

}
