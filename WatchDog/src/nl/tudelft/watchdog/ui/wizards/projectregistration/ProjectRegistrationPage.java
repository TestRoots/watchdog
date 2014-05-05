package nl.tudelft.watchdog.ui.wizards.projectregistration;

import nl.tudelft.watchdog.ui.UIUtils;
import nl.tudelft.watchdog.ui.wizards.FinishableWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

/**
 * A Page that allows the user to enter how much time he spend on testing vs.
 * production code.
 *
 */
class ProjectRegistrationPage extends FinishableWizardPage {

	private Text projectName;
	private Text roleUser;

	/** Constructor. */
	protected ProjectRegistrationPage() {
		super("Register Project");
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Register a new project");
		Composite topComposite = createComposite(parent);
		setControl(topComposite);
		setPageComplete(false);
	}

	/**
	 * Creates a slider so users can choose how much time they think they spend
	 * to testing vs. production code.
	 */
	private Composite createComposite(Composite parent) {
		Composite topComposite = UIUtils.createGridedComposite(parent, 1);
		topComposite.setLayoutData(UIUtils.createFullGridUsageData());

		UIUtils.createBoldLabel("Register a new project", topComposite);
		UIUtils.createLabel(
				"Please, describe your project as best as you can.",
				topComposite);

		Composite composite = UIUtils.createGridedComposite(topComposite, 2);
		composite.setLayoutData(UIUtils.createFullGridUsageData());
		projectName = UIUtils
				.createLinkedFieldInput(
						"Project Name: ",
						"The name of the project you work on in this workspace (if you have a website, we'd love to see that!)",
						composite);
		roleUser = UIUtils.createLinkedFieldInput("Your Role: ",
				"Try best describe what you do: Developer, Tester, ...",
				composite);
		UIUtils.createLabel("", composite);
		UIUtils.createLabel("", composite);

		createSimpleYesNoQuestion("Does your project use JUnit?", composite);
		createSimpleYesNoQuestion(
				"Does your project use JUnit for unit testing?", composite);
		createSimpleYesNoQuestion("Are all your Junit tests unit tests?",
				composite);
		createSimpleYesNoQuestion(
				"Does your project use testing frameworks\n or programs other than JUnit?",
				composite);

		// UIUtils.createLabel(
		// "Please provide an estimate of how you spend your time in Eclipse.",
		// composite);

		Composite row = UIUtils.createGridedComposite(parent, 3);
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
		return composite;
	}

	/** Creates a simple question with according yes/no radio buttons. */
	private void createSimpleYesNoQuestion(String question, Composite parent) {
		UIUtils.createLabel(question, parent);
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		composite.setLayout(new FillLayout());
		UIUtils.createRadioButton(composite, "Yes");
		UIUtils.createRadioButton(composite, "No");
	}

	@Override
	public boolean canFinish() {
		if (getErrorMessage() == null) {
			return true;
		}
		return false;
	}
}
