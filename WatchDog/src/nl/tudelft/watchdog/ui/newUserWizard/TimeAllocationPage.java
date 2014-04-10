package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

/**
 * A Page that allows the user to enter how much time he spend on testing vs.
 * production code.
 *
 */
public class TimeAllocationPage extends WizardPage {

	/** Constructor. */
	protected TimeAllocationPage() {
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
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayoutData(UIUtils.fullGirdUsageData);
		UIUtils.createLabel("Estimate how you spend your time in Eclipse.",
				composite);

		Composite row = UIUtils.createGridedComposite(composite, 3);
		row.setLayoutData(UIUtils.fullGirdUsageData);
		UIUtils.createLabel("100% Testing  ", row);
		final Slider slider = new Slider(row, SWT.NONE);
		slider.setLayoutData(UIUtils.fullGirdUsageData);
		slider.setValues(50, 0, 105, 5, 5, 5);
		UIUtils.createLabel("  100% Production", row);
		UIUtils.createLabel("", row);
		final Label sliderValueText = UIUtils.createLabel(
				"50% Testing, 50% Production", row);
		sliderValueText.setLayoutData(UIUtils.fullGirdUsageData);
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
}
