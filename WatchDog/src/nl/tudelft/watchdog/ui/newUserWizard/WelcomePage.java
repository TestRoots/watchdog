package nl.tudelft.watchdog.ui.newUserWizard;

import java.net.MalformedURLException;
import java.net.URL;

import nl.tudelft.watchdog.ui.UIUtils;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * The first page of the {@link NewUserWizard}. It asks the question: Are you a
 * new WatchDog user, yes or no? Depending on the answer, it dynamically
 * displays the information we are interested in.
 */
class WelcomePage extends WizardPage {

	/** The length (in characters) of the WatchDog userid. */
	private static final int idLength = 40;

	/**
	 * The Composite which holds the text field for the new user welcome or
	 * holds the text field for the existing user login..
	 */
	private Composite dynamicContent;

	/**
	 * The userid as entered by the user (note: as delivered from this wizard
	 * page, still unchecked).
	 */
	private Text useridInput;

	/** The no button from the question. */
	private Button radioButtonNo;

	/** Constructor. */
	WelcomePage() {
		super("Welcome to WatchDog!");
		setTitle("Welcome to WatchDog!");
		setDescription("This wizard will guide you through the setup of a WatchDog User. May we ask for one minute of your time?");
	}

	@Override
	public void createControl(Composite parent) {
		Composite topContainer = UIUtils.createGridedComposite(parent, 1);

		// Sets up the basis layout
		createQuestionComposite(topContainer);

		// Required to avoid an error in the wizard system
		setControl(topContainer);
		setPageComplete(false);
	}

	/**
	 * Creates and returns the question whether WatchDog Id is already known.
	 */
	private Composite createQuestionComposite(final Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 2);
		UIUtils.createLabel("Do you already have a WatchDog Userid? ",
				composite);

		final Composite radioButtons = UIUtils.createGridedComposite(composite,
				1);
		radioButtons.setLayout(new FillLayout());
		final Button radioButtonYes = UIUtils.createRadioButton(radioButtons,
				"Yes");
		radioButtonYes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeDynamicContent(parent);
				dynamicContent = createLoginComposite(parent);
				parent.layout();
				setPageComplete(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		radioButtonNo = UIUtils.createRadioButton(radioButtons, "No");
		radioButtonNo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeDynamicContent(parent);
				dynamicContent = createWelcomeComposite(parent);
				parent.layout();
				setPageComplete(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return composite;
	}

	/** Removes the dynamic content from the page, if it exists. */
	private void removeDynamicContent(final Composite parent) {
		if (dynamicContent != null) {
			dynamicContent.dispose();
			parent.layout();
			parent.update();
		}
	}

	/**
	 * Creates and returns an input field, in which user can enter their
	 * existing WatchDog ID.
	 */
	private Composite createLoginComposite(Composite parent) {
		String labelText = "Your WatchDog User ID: ";
		String inputToolTip = "The User ID we sent you upon your first WatchDog registration.";

		Composite composite = UIUtils.createGridedComposite(parent, 2);
		composite.setLayoutData(UIUtils.createFullGridUsageData());

		useridInput = UIUtils.createLinkedFieldInput(labelText, inputToolTip,
				composite);
		useridInput.setTextLimit(idLength);
		useridInput.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (useridInput.getText().length() == idLength) {
					setErrorMessage(null);
					setPageComplete(true);
				} else {
					setErrorMessage("Not a valid id.");
					setPageComplete(false);
				}
				getWizard().getContainer().updateButtons();
			}
		});

		return composite;
	}

	/** Creates and returns a welcoming composite for new users. */
	private Composite createWelcomeComposite(Composite parent) {
		Composite composite = UIUtils.createGridedComposite(parent, 1);
		createSeparator(composite);
		UIUtils.createBoldLabel("Welcome, new WatchDog User!", composite);
		UIUtils.createLabel("", composite);

		Link linkedText = new Link(composite, SWT.WRAP);
		linkedText
				.setText("WatchDog keeps track of the way you develop and test your software. Your usage data is sent to and maintained by the TestRoots team at Delft University. We are never going to do anything bad with it.\n\nYou can stay completely anonymous. But our research greatly improves, if you provide us with a bit of info about you. This way, you can also win one of our amazing prices.\n\nIf you want to know more about WatchDog (or the prices to win), visit our website <a href=\"http://watchdog.testroots.org\">watchdog.testroots.org</a>.");
		GridData labelData = new GridData();
		labelData.widthHint = parent.getClientArea().width - 30;
		linkedText.setLayoutData(labelData);
		linkedText.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					// Open default external browser
					PlatformUI.getWorkbench().getBrowserSupport()
							.getExternalBrowser().openURL(new URL(event.text));
				} catch (PartInitException | MalformedURLException exception) {
					// Browser could not be opened. We do nothing about it.
				}
			}
		});
		setErrorMessage(null);

		return composite;
	}

	/** Creates a horizontal separator. */
	private void createSeparator(Composite parent) {
		Label separator = UIUtils.createLabel("", SWT.SEPARATOR
				| SWT.HORIZONTAL | SWT.FILL, parent);
		GridData layoutData = UIUtils.createFullGridUsageData();
		layoutData.horizontalSpan = 2;
		separator.setLayoutData(layoutData);
	}

	/** @return Whether a possibly valid user id has been entered. */
	public boolean hasValidUserId() {
		return useridInput != null && !useridInput.isDisposed()
				&& getErrorMessage() == null && isPageComplete();
	}

	/** @return The userid entered by the user. */
	/* package */String getUserId() {
		return useridInput.getText();
	}

	/**
	 * @return Whether the user wants to create a new user (<code>true</code> in
	 *         that case, <code>false</code> otherwise).
	 */
	public boolean getRegisterNewUser() {
		return radioButtonNo.getSelection();
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage();
	}
}