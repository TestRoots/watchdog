package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

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

	/** Constructor. */
	protected ProjectSliderPage() {
		super("Time Distrubtion");
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Register a new project");

		Composite topComposite = UIUtils.createGridedComposite(parent, 1);
		topComposite.setLayoutData(UIUtils.createFullGridUsageData());

		UIUtils.createLabel(
				"Please provide an estimate of how you spend your time in Eclipse.",
				topComposite);

		Composite row = UIUtils.createGridedComposite(topComposite, 3);
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
				setErrorMessageAndPageComplete(null);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		setErrorMessage("Move slider to estimate your personal time distribution.");

		setPageComplete(false);
		setControl(topComposite);
	}

	@Override
	public boolean canFinish() {
		return false;
	}

}
