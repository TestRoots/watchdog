package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.ui.wizards.FormValidationListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

/**
 * A Page displaying a slider so that the user can estimate how his or her time
 * is distributed between production code and testing.
 */
public class ProjectSliderPage extends FinishableWizardPage {

	private Composite junitForTestingOnly;
	private Composite testDrivenDesing;
	private boolean sliderTouched = false;

	/** Constructor. */
	protected ProjectSliderPage() {
		super("Time Distrubtion");
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Last question ...");
		setDescription("You're nearly there, hold on :).");

		Composite topComposite = UIUtils.createGridedComposite(parent, 1);
		topComposite.setLayoutData(UIUtils.createFullGridUsageData());

		UIUtils.createBoldLabel(
				"Estimate your time distribution for this workspace",
				topComposite);
		UIUtils.createLabel(
				"Estimate how you divide your time into the two activities testing and production. Just have a wild guess!\n",
				topComposite);

		Composite row = UIUtils.createGridedComposite(topComposite, 3);
		row.setLayoutData(UIUtils.createFullGridUsageData());
		Label testingLabel = UIUtils.createLabel("100% Testing  ", row);
		testingLabel
				.setToolTipText("To the testing activity, everything you do with Junit tests counts. Examples: writing, modifying, debugging, and executing Junit tests");
		final Slider slider = new Slider(row, SWT.NONE);
		slider.setLayoutData(UIUtils.createFullGridUsageData());
		slider.setValues(50, 0, 105, 5, 5, 5);
		Label productionLabel = UIUtils.createLabel("  100% Production", row);
		productionLabel
				.setToolTipText("To the production activity, every activity that has to do with regular, non-test production code counts.");
		UIUtils.createLabel("", row);
		final Label sliderValueText = UIUtils.createItalicLabel(
				"50% Testing, 50% Production", row);
		sliderValueText.setLayoutData(UIUtils.createFullGridUsageData());
		sliderValueText.setAlignment(SWT.CENTER);

		slider.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int developmentTimeValue = slider.getSelection();
				int testingTimeValue = 100 - developmentTimeValue;
				sliderValueText.setText(testingTimeValue + "% Testing, "
						+ developmentTimeValue + "% Production");
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

		UIUtils.createBoldLabel("Questions on how you test", topComposite);
		Composite questionComposite = UIUtils.createFullGridedComposite(
				topComposite, 2);
		junitForTestingOnly = createSimpleYesNoDontKnowQuestion(
				"Do you use JUnit for isolated unit testing only \n (i.e. only one class tested per test case)? ",
				questionComposite);
		testDrivenDesing = createSimpleYesNoDontKnowQuestion(
				"Do you follow Test-Driven Design or similar practices (Test-first, ...)? ",
				questionComposite);
		FormValidationListener formValidationListener = new FormValidationListener(
				this);
		addValidationListenerToAllChildren(junitForTestingOnly,
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
		} else if (!hasOneSelection(junitForTestingOnly)
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

}
