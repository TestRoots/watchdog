package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.ui.wizards.YesNoDontKnowChoice;
import nl.tudelft.watchdog.util.WatchDogUtils;

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

	private static final String DOES_YOUR_PROJECT = "Does your project use ...";

	private static final String DOES_AT_LEAST_ONE_PROJECT_USE = "Does at least one of your projects use ...";

	/** Project name. */
	Text projectNameInput;

	/** Project website */
	Text projectWebsite;

	private Label multipleProjectLabel;

	private Composite noSingleProjectComposite;

	private Composite useJunit;

	private Composite otherTestingFrameworks;

	private Composite otherTestingForms;

	/** No, these projects do not belong to a single physical project. */
	Button noSingleProjectButton;

	/** Constructor. */
	protected ProjectRegistrationPage() {
		super("Register Project");
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Register a new project (2/3)");
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

		Composite composite = UIUtils.createGridedComposite(topComposite, 2);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		noSingleProjectComposite = createSimpleYesNoQuestion(
				"All projects in this workspace belong to one ('bigger') project? ",
				composite);
		noSingleProjectButton = (Button) noSingleProjectComposite.getChildren()[1];
		final Button yesSingleProjectButton = (Button) noSingleProjectComposite
				.getChildren()[0];

		noSingleProjectButton
				.addSelectionListener(new SingleProjectSelectionListener(
						noSingleProjectButton, parent));
		yesSingleProjectButton
				.addSelectionListener(new SingleProjectSelectionListener(
						noSingleProjectButton, parent));

		Composite textInputComposite = UIUtils.createFullGridedComposite(
				topComposite, 2);
		projectNameInput = UIUtils.createLinkedFieldInput("Project Name: ",
				"The name of the project(s) you work on in this workspace",
				textInputComposite);
		projectWebsite = UIUtils.createLinkedFieldInput("Project Website: ",
				"If you have a website, we'd love to see it here.",
				textInputComposite);

		FormValidationListener formValidationListener = new FormValidationListener(
				this);
		projectNameInput.addModifyListener(formValidationListener);
		projectWebsite.addModifyListener(formValidationListener);

		Composite questionComposite = UIUtils.createFullGridedComposite(
				topComposite, 2);

		UIUtils.createLabel("", questionComposite);
		UIUtils.createLabel("", questionComposite);
		multipleProjectLabel = UIUtils.createLabel(DOES_YOUR_PROJECT,
				questionComposite);
		UIUtils.createLabel("", questionComposite);
		useJunit = createSimpleYesNoDontKnowQuestion("  ... JUnit? ",
				questionComposite);
		otherTestingFrameworks = createSimpleYesNoDontKnowQuestion(
				"  ... other testing frameworks (e.g. Mockito)? ",
				questionComposite);
		addValidationListenerToAllChildren(useJunit, formValidationListener);
		addValidationListenerToAllChildren(otherTestingFrameworks,
				formValidationListener);
		otherTestingForms = createSimpleYesNoDontKnowQuestion(
				"  ... other testing forms (e.g. manual testing)? ",
				questionComposite);
		addValidationListenerToAllChildren(useJunit, formValidationListener);
		addValidationListenerToAllChildren(otherTestingFrameworks,
				formValidationListener);
		addValidationListenerToAllChildren(otherTestingForms,
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
		} else if (!hasOneSelection(useJunit)
				|| !hasOneSelection(otherTestingFrameworks)
				|| !hasOneSelection(otherTestingForms)) {
			setErrorMessageAndPageComplete("Please answer all yes/no/don't know questions!");
		} else {
			setErrorMessageAndPageComplete(null);
		}
		getWizard().getContainer().updateButtons();
	}

	private boolean inputFieldDoesNotHaveMinimumSensibleInput(Text input) {
		return WatchDogUtils.isEmptyOrHasOnlyWhitespaces(input.getText())
				|| input.getText().length() < 3;
	}

	@Override
	public boolean canFinish() {
		return false;
	}

	/**
	 * @return Whether or not this project uses Junit.
	 */
	/* package */YesNoDontKnowChoice usesJunit() {
		return evaluateWhichSelection(useJunit);
	}

	/**
	 * @return Whether or not this project uses other testing frameworks (than
	 *         Junit).
	 */
	/* package */YesNoDontKnowChoice usesOtherTestingFrameworks() {
		return evaluateWhichSelection(otherTestingFrameworks);
	}

	/**
	 * @return Whether or not this project uses other testing strategies.
	 */
	/* package */YesNoDontKnowChoice usesOtherTestingForms() {
		return evaluateWhichSelection(otherTestingForms);
	}

	private class SingleProjectSelectionListener implements SelectionListener {
		private final Button noSingleProjectButton;
		private final Composite parent;

		private SingleProjectSelectionListener(Button noSingleProjectButton,
				Composite parent) {
			this.noSingleProjectButton = noSingleProjectButton;
			this.parent = parent;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (noSingleProjectButton.getSelection()) {
				performUIUpdate(DOES_AT_LEAST_ONE_PROJECT_USE, false);
			} else {
				performUIUpdate(DOES_YOUR_PROJECT, true);
			}
		}

		private void performUIUpdate(String label, boolean enableState) {
			multipleProjectLabel.setText(label);
			multipleProjectLabel.update();
			multipleProjectLabel.pack();
			projectNameInput.setEnabled(enableState);
			projectWebsite.setEnabled(enableState);
			parent.update();
			validateFormInputs();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	}

}
