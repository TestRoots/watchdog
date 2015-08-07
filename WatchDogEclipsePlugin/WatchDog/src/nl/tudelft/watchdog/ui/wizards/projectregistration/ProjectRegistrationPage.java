package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.core.ui.wizards.YesNoDontKnowChoice;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.util.WatchDogUtils;

import org.eclipse.jface.wizard.IWizardPage;
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
public class ProjectRegistrationPage extends FinishableWizardPage {

	private static final String TITLE = "Register a new project";

	private static final String DOES_YOUR_PROJECT = "Does your project use ...";

	private static final String DOES_AT_LEAST_ONE_PROJECT_USE = "Does at least one of your projects use ...";

	/** Project name. */
	Text projectNameInput;

	/** Project website */
	Text projectWebsite;

	private Label multipleProjectLabel;

	private Composite noSingleProjectComposite;

	private Composite useContinuousIntegration;

	private Composite useJunit;

	private Composite otherTestingFrameworks;

	private Composite otherTestingForms;

	/** No, these projects do not belong to a single physical project. */
	Button noSingleProjectButton;

	/** Constructor. */
	public ProjectRegistrationPage(int pageNumber) {
		super("Register Project", pageNumber);
	}

	@Override
	public void createControl(Composite parent) {
		setTitle(TITLE + " (" + currentPageNumber + "/"
				+ ((RegistrationWizardBase) getWizard()).getTotalPages() + ")");
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
				"Do all Eclipse projects in this workspace belong to one 'larger' project? ",
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

		useContinuousIntegration = createSimpleYesNoDontKnowQuestion(
				"Does your project use Continuous Integration (Travis, Jenkins, etc.)?",
				questionComposite);

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
		otherTestingForms = createSimpleYesNoDontKnowQuestion(
				"  ... other testing forms (e.g. manual testing)? ",
				questionComposite);

		addValidationListenerToAllChildren(useContinuousIntegration,
				formValidationListener);
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
			setErrorMessageAndPageComplete("You must enter a project name longer than 2 chars.");
		} else if (!hasOneSelection(useContinuousIntegration)
				|| !hasOneSelection(useJunit)
				|| !hasOneSelection(otherTestingFrameworks)
				|| !hasOneSelection(otherTestingForms)) {
			setErrorMessageAndPageComplete("Please answer all yes/no/don't know questions!");
		} else {
			setErrorMessageAndPageComplete(null);
		}

		setTitle(TITLE + " (" + currentPageNumber + "/"
				+ ((RegistrationWizardBase) getWizard()).getTotalPages() + ")");
		if (shouldSkipProjectSliderPage()) {
			setTitle(TITLE
					+ " ("
					+ currentPageNumber
					+ "/"
					+ (((RegistrationWizardBase) getWizard()).getTotalPages() - 1)
					+ ")");
		}
		getWizard().getContainer().updateButtons();
	}

	private boolean inputFieldDoesNotHaveMinimumSensibleInput(Text input) {
		return WatchDogUtils.isEmptyOrHasOnlyWhitespaces(input.getText())
				|| input.getText().length() < 3;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return getWizard().getPreviousPage(this);
	}

	@Override
	public boolean canFinish() {
		return false;
	}

	/**
	 * @return Whether or not this project uses Continuous Integration tools.
	 */
	/* package */YesNoDontKnowChoice usesContinuousIntegration() {
		return evaluateWhichSelection(useContinuousIntegration);
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

	/**
	 * @return Whether the {@link ProjectSliderPage}, which logically follows
	 *         this page in the wizard, can be skipped (because the selection by
	 *         the user in this wizard don't make sense to fill-out the next
	 *         page).
	 */
	public boolean shouldSkipProjectSliderPage() {
		return usesOtherTestingFrameworks() == YesNoDontKnowChoice.No
				&& usesJunit() == YesNoDontKnowChoice.No;
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
