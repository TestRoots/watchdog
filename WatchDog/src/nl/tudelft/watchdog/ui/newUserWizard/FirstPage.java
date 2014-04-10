package nl.tudelft.watchdog.ui.newUserWizard;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * The first page of the {@link NewUserWizard}. It asks the question: Are you a
 * new WatchDog user, yes or no? Depending on the answer, it dynamically
 * displays the information we are interested in.
 */
class FirstPage extends WizardPage {

	/** The length (in characters) of the WatchDog userid. */
	private static final int useridLength = 40;

	/**
	 * The Composite which holds the text field for the existing user login.
	 */
	private Composite existingUserLogin;

	/**
	 * The Composite which holds the text field for the new user welcome.
	 */
	private Composite welcomeUser;

	/**
	 * The userid as entered by the user (note: at this point, still unchecked).
	 */
	private Text userid;

	/** Constructor. */
	FirstPage() {
		super("Welcome to WatchDog!");
		setTitle("Welcome to WatchDog!");
		setDescription("This wizard will guide you through the setup of WatchDog. May we ask for one minute of your time?");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topContainer = UIUtils.createGridedComposite(parent, 1);

		// Sets up the basis layout
		createQuestionComposite(topContainer);
		existingUserLogin = createLoginComposite(topContainer);
		welcomeUser = createWelcomeComposite(topContainer);

		// Required to avoid an error in the system
		setControl(topContainer);
		setPageComplete(false);
	}

	/**
	 * Creates and returns the question whether WatchDog Id is already known.
	 */
	private Composite createQuestionComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 2);

		UIUtils.createLabel("Is this the first time you install WatchDog?",
				composite);

		final Composite radioButtons = UIUtils.createGridedComposite(composite,
				1);
		radioButtons.setLayout(new FillLayout());
		final Button radioButtonYes = new Button(radioButtons, SWT.RADIO);
		radioButtonYes.setText("Yes");
		radioButtonYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				welcomeUser.setVisible(radioButtonYes.getSelection());
				setPageComplete(true);
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
				existingUserLogin.setVisible(radioButtonNo.getSelection());
				setPageComplete(false);
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
		Composite composite = UIUtils.createGridedComposite(parent, 2);
		composite.setLayoutData(UIUtils.fullGirdUsageData);
		composite.setVisible(false);

		UIUtils.createLabel("Your WatchDog Id:", composite);

		userid = UIUtils.createTextInput(composite);
		userid.setTextLimit(useridLength);
		userid.setToolTipText("The User id we sent you upon your last WatchDog registration. You may (and should!) reuse your WatchDog Id when you install a new Eclipse version for the same purpose.");
		userid.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (userid.getText().length() == useridLength) {
					setErrorMessage(null);
					setPageComplete(true);
					getWizard().getContainer().updateButtons();
				} else {
					setErrorMessage("Not a valid user id.");
					setPageComplete(false);
					getWizard().getContainer().updateButtons();
				}
			}
		});

		return composite;
	}

	/**
	 * Creates and returns a welcoming composite for new users.
	 */
	private Composite createWelcomeComposite(Composite topContainer2) {
		Composite composite = UIUtils.createGridedComposite(topContainer2, 2);
		composite.setVisible(false);
		UIUtils.createLabel("Welcome, you new User!", composite);

		return composite;
	}

	/**
	 * @return Whether a possibly valid user id has been entered.
	 */
	public boolean hasValidUserId() {
		return existingUserLogin.isVisible() && getErrorMessage() == null
				&& isPageComplete();
	}

	/**
	 * @return The userid entered by the user.
	 */
	/* package */String getUserId() {
		return userid.getText();
	}

}