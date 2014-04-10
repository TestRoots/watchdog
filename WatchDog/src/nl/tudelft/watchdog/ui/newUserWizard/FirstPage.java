package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

/**
 * The first page of the {@link NewUserWizard}. It asks the question: Are you a
 * new WatchDog user, yes or no? Depending on the answer, it dynamically
 * displays the information we are interested in.
 */
class FirstPage extends WizardPage {

	/**
	 * The Composite which holds the text field for the existing user login.
	 */
	private Composite existingUserLogin;

	/**
	 * The Composite which holds the text field for the existing user
	 * registration.
	 */
	private Composite newUserRegistration;

	/** Constant for full horizontal usage of Grid data. */
	private final GridData fullGirdUsageData = new GridData(SWT.FILL, SWT.NONE,
			true, false);

	/** Constructor. */
	FirstPage() {
		super("Welcome to WatchDog!");
		setTitle("Welcome to WatchDog!");
		setDescription("This wizard will guide you through the setup of WatchDog. May we ask for one minute of your time?");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topContainer = createGridedComposite(parent, 1);

		// Sets up the basis layout
		Composite questionContainer = createQuestionComposite(topContainer);
		existingUserLogin = createLoginComposite(topContainer);
		newUserRegistration = createRegistrationComposite(topContainer);

		// Required to avoid an error in the system
		setControl(questionContainer);
		setPageComplete(false);

	}

	/**
	 * Creates and returns the question whether WatchDog Id is already known.
	 */
	private Composite createQuestionComposite(Composite parent) {
		Composite composite = createGridedComposite(parent, 2);

		UIUtils.createLabel("Is this the first time you install WatchDog?",
				composite);

		final Composite radioButtons = createGridedComposite(composite, 1);
		radioButtons.setLayout(new FillLayout());
		final Button radioButtonYes = new Button(radioButtons, SWT.RADIO);
		radioButtonYes.setText("Yes");
		radioButtonYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean visible = radioButtonYes.getSelection();
				newUserRegistration.setVisible(visible);
				existingUserLogin.setVisible(!visible);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		final Button radioButtonNo = new Button(radioButtons, SWT.RADIO);
		radioButtonNo.setText("No");
		radioButtonNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean visible = radioButtonNo.getSelection();
				existingUserLogin.setVisible(radioButtonNo.getSelection());
				newUserRegistration.setVisible(!visible);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return composite;
	}

	/**
	 * Creates and returns an input field, in which user can enter their
	 * existing WatchDog ID.
	 */
	private Composite createLoginComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(fullGirdUsageData);
		composite.setVisible(false);

		UIUtils.createLabel("Your WatchDog Id:", composite);

		Text text = createTextInput(composite);
		text.setTextLimit(40);
		text.setToolTipText("The SHA-1 hash that was returned upon your last WatchDog registration. You may (and should!) reuse your registration when you install a new Eclipse version for the same purpose.");
		return composite;
	}

	/** Creates and returns the form of the registration. */
	private Composite createRegistrationComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(2, false));
		GridData fullGirdUsageData = new GridData(SWT.FILL, SWT.NONE, true,
				false);
		composite.setLayoutData(fullGirdUsageData);
		composite.setVisible(false);

		UIUtils.createLabel("Your Name:", composite);
		createTextInput(composite);
		UIUtils.createLabel("Your eMail:", composite);
		createTextInput(composite);
		UIUtils.createLabel("Your Organization/Company:", composite);
		createTextInput(composite);
		UIUtils.createLabel("Your Project:", composite);
		createTextInput(composite);
		UIUtils.createLabel("Your Role in the Project:", composite);
		createTextInput(composite);

		UIUtils.createLabel("Does your project use JUnit?", composite);
		createTextInput(composite);

		UIUtils.createLabel(
				"Estimate how you distribute your development time?", composite);

		Composite row = createGridedComposite(composite, 3);
		row.setLayoutData(fullGirdUsageData);
		UIUtils.createLabel("100% Testing", row);
		final Slider slider = new Slider(row, SWT.NONE);
		slider.setLayoutData(fullGirdUsageData);
		slider.setValues(50, 0, 105, 5, 5, 5);
		UIUtils.createLabel("100% Developing", row);
		UIUtils.createLabel("", row);
		final Label sliderValueText = UIUtils.createLabel(
				"50% Testing, 50% Developing", row);

		slider.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int developmentTimeValue = slider.getSelection();
				int testingTimeValue = 100 - developmentTimeValue;
				sliderValueText.setText(testingTimeValue + "% Testing, "
						+ developmentTimeValue + "% Developing");
				sliderValueText.update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return composite;
	}

	/**
	 * @return A {@link GridLayout}ed composite with the given number of
	 *         columns.
	 */
	private Composite createGridedComposite(Composite parent, int columns) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(columns, false));
		return composite;
	}

	/** Creates and returns a user text input field. */
	Text createTextInput(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(fullGirdUsageData);
		return text;
	}

}