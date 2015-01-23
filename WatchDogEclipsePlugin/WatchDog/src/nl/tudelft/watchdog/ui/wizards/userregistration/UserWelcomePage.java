package nl.tudelft.watchdog.ui.wizards.userregistration;

import nl.tudelft.watchdog.ui.util.BrowserOpenerSelection;
import nl.tudelft.watchdog.ui.util.UIUtils;
import nl.tudelft.watchdog.ui.wizards.WelcomePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

/**
 * The first page of the {@link UserRegistrationWizard}. It asks the question:
 * Are you a new WatchDog user, yes or no? Depending on the answer, it
 * dynamically displays the information we are interested in.
 */
class UserWelcomePage extends WelcomePage {

	private Link linkedTextLink;
	private Label welcomeTextLabel;

	/** Constructor. */
	UserWelcomePage() {
		super("Welcome to WatchDog!");
		setDescription("This wizard guides you through the setup of a WatchDog User.");
		welcomeTitle = "Welcome! Registering a new user takes just 1 minute!";
		welcomeText = "";
		labelText = "Your WatchDog User-ID: ";
		inputToolTip = "The User-ID we sent you upon your first WatchDog registration.";
		labelQuestion = "Do you want to register a new user? ";
		currentRegistration = "User";
		pageNumber = 1;
	}

	@Override
	protected String getIconPath() {
		return "resources/images/user.png";
	}

	@Override
	public void createControl(Composite parent) {
		Composite topContainer = UIUtils.createFullGridedComposite(parent, 1);

		createWatchDogDescription(topContainer);
		createLogoRow(topContainer);
		createQuestionComposite(topContainer);

		setControl(topContainer);
		setPageComplete(false);
	}

	private Composite createWatchDogDescription(Composite parent) {

		Composite composite = UIUtils.createFullGridedComposite(parent, 1);

		welcomeTextLabel = UIUtils.createBoldLabel("", SWT.WRAP, composite);

		linkedTextLink = new Link(composite, SWT.WRAP);
		linkedTextLink.setText("");
		linkedTextLink.addSelectionListener(new BrowserOpenerSelection());

		return composite;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			welcomeTextLabel
					.setText("WatchDog is a free, open-source plugin that tells how you code your software.");
			welcomeTextLabel.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			welcomeTextLabel.getParent().layout();
			welcomeTextLabel.getParent().update();

			String descriptionText = "It measures how you write Java code and tests. We never do anything bad with <a href=\"http://www.testroots.org/testroots_watchdog.html#details\">your purely numerical data</a>.\nYou can win amazing <a href=\"http://www.testroots.org/testroots_watchdog.html#prizes\">prizes,</a> a detailed report and our eternal gratitude! :-)\n";

			linkedTextLink.setText(descriptionText);
			linkedTextLink
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			linkedTextLink.getParent().layout();
			linkedTextLink.getParent().update();
			linkedTextLink.getParent().getParent().layout();
			linkedTextLink.getParent().getParent().update();
		}
	}
}