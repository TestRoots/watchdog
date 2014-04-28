package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

/**
 * A Page that allows the user to enter how much time he spend on testing vs.
 * production code.
 *
 */
class ProjectRegistrationPage extends WizardPage {

	/** Constructor. */
	protected ProjectRegistrationPage() {
		super("Time Distribution");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topComposite = createTimeSlider(parent);

		// Required to avoid an error in the system
		setControl(topComposite);
		setPageComplete(false);
	}

	/**
	 * Creates a slider so users can choose how much time they think they spend
	 * to testing vs. production code.
	 * 
	 * @return
	 */
	private Composite createTimeSlider(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 2);

		UIUtils.createLabel("Your Project:", composite);
		UIUtils.createTextInput(composite);

		UIUtils.createLabel("Your Role:", composite);
		UIUtils.createTextInput(composite);

		createSimpleYesNoQuestion("Does your project use JUnit?", composite);
		createSimpleYesNoQuestion(
				"Does your project use JUnit for unit testing?", composite);
		createSimpleYesNoQuestion(
				"Does your project use other testing frameworks/strategies than JUnit?",
				composite);

		composite.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createLabel(
				"Please provide an estimate of how you spend your time in Eclipse.",
				composite);

		Composite row = UIUtils.createGridedComposite(composite, 3);
		row.setLayoutData(UIUtils.createFullGridUsageData());
		UIUtils.createLabel("100% Testing  ", row);
		final Slider slider = new Slider(row, SWT.NONE);
		slider.setLayoutData(UIUtils.createFullGridUsageData());
		slider.setValues(50, 0, 105, 5, 5, 5);
		UIUtils.createLabel("  100% Production", row);
		UIUtils.createLabel("", row);
		final Label sliderValueText = UIUtils.createLabel(
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
				setErrorMessage(null);
				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		setErrorMessage("Move slider to how you estimate your personal time distribution.");
		return composite;
	}

	/**
	 * Creates a simple question with according yes/no radio buttons.
	 */
	private void createSimpleYesNoQuestion(String question, Composite parent) {
		UIUtils.createLabel("Does your project use JUnit?", parent);
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayout(new FillLayout());
		UIUtils.createRadioButton(composite, "Yes");
		UIUtils.createRadioButton(composite, "No");
	}
}
