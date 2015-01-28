package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.ui.wizards.YesNoDontKnowChoice;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

/**
 * A Page displaying a slider so that the user can estimate how his or her time
 * is distributed between production code and testing.
 */
public class ProjectSliderPage extends FinishableWizardPage {

	private Composite junitForUnitTestingOnly;
	private Composite testDrivenDesing;
	private boolean sliderTouched = false;

	/**
	 * The slider. Its value denotes in full percentage how much production code
	 * the user estimates to write.
	 */
	protected Scale percentageProductionSlider;

	/** Constructor. */
	public ProjectSliderPage(int pageNumber) {
		super("Time Distrubtion", pageNumber);
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Register a new project (" + currentPageNumber + "/"
				+ ((RegistrationWizardBase) getWizard()).getTotalPageNumber() + ")");
		setDescription("You nearly made it! Only this page left.");

		Composite topComposite = UIUtils.createFullGridedComposite(parent, 1);

		UIUtils.createLabel(
				"Estimate how you divide your time into the two activities testing and production. Just have a wild guess!\n",
				topComposite);

		Composite row = UIUtils.createFullGridedComposite(topComposite, 3);
		Label testingLabel = UIUtils.createLabel("100% Testing  ", row);
		testingLabel
				.setToolTipText("To the testing activity, everything you do with Junit tests counts. Examples: writing, modifying, debugging, and executing Junit tests");
		percentageProductionSlider = new Scale(row, SWT.HORIZONTAL);
		percentageProductionSlider.setLayoutData(UIUtils
				.createFullGridUsageData());
		percentageProductionSlider.setSelection(50);
		percentageProductionSlider.setIncrement(5);
		percentageProductionSlider.setPageIncrement(5);
		percentageProductionSlider.setMaximum(100);
		percentageProductionSlider.setMinimum(0);
		Label productionLabel = UIUtils.createLabel("  100% Production", row);
		productionLabel
				.setToolTipText("To the production activity, every activity that has to do with regular, non-test production code counts.");
		UIUtils.createLabel("", row);
		final Label sliderValueText = UIUtils.createItalicLabel(
				"50% Testing, 50% Production", row);
		sliderValueText.setLayoutData(UIUtils.createFullGridUsageData());
		sliderValueText.setAlignment(SWT.CENTER);
		percentageProductionSlider
				.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int developmentTimeValue = percentageProductionSlider
								.getSelection();
						int testingTimeValue = 100 - developmentTimeValue;
						sliderValueText.setText(testingTimeValue
								+ "% Testing, " + developmentTimeValue
								+ "% Production");
						sliderValueText.update();
						sliderTouched = true;
						validateFormInputs();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		UIUtils.createLabel(
				"Testing is every activity related to testing (reading, writing, modifying, refactoring and executing JUnit tests).\nProduction is every activity related to regular code (reading, writing, modifying, and refactoring Java classes).\n",
				topComposite);

		Composite questionComposite = UIUtils.createFullGridedComposite(
				topComposite, 2);
		junitForUnitTestingOnly = createSimpleYesNoDontKnowQuestion(
				"Do you use JUnit only for unit testing \n (i.e. only one production class tested per Junit test class)? ",
				questionComposite);
		testDrivenDesing = createSimpleYesNoDontKnowQuestion(
				"Do you follow Test-Driven Design or similar practices (Test-first)? ",
				questionComposite);
		FormValidationListener formValidationListener = new FormValidationListener(
				this);
		addValidationListenerToAllChildren(junitForUnitTestingOnly,
				formValidationListener);
		addValidationListenerToAllChildren(testDrivenDesing,
				formValidationListener);

		setPageComplete(false);
		setControl(topComposite);
	}

	@Override
	public void validateFormInputs() {
		if (!sliderTouched) {
			setErrorMessage("Move the slider to estimate your personal time distribution.");
		} else if (!hasOneSelection(junitForUnitTestingOnly)
				|| !hasOneSelection(testDrivenDesing)) {
			setErrorMessageAndPageComplete("Please answer all yes/no/don't know questions!");
		} else {
			setErrorMessageAndPageComplete(null);
		}
		getWizard().getContainer().updateButtons();
	}

	@Override
	public boolean canFinish() {
		return false;
	}

	/**
	 * @return Whether this project uses Junit for Unit testing only.
	 */
	/* package */YesNoDontKnowChoice usesJunitForUnitTestingOnly() {
		return evaluateWhichSelection(junitForUnitTestingOnly);
	}

	/**
	 * @return Whether this project uses TDD.
	 */
	/* package */YesNoDontKnowChoice usesTestDrivenDesing() {
		return evaluateWhichSelection(testDrivenDesing);
	}

}
