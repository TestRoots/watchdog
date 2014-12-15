package nl.tudelft.watchdog.ui.wizards;

import nl.tudelft.watchdog.ui.util.UIUtils;

import org.eclipse.swt.widgets.Composite;

/**
 * Possible finishing page in the wizard. If the user exists on the server, or
 * the server is not reachable, the user can exit here.
 */
public abstract class RegistrationEndingPage extends FinishableWizardPage {

	/** The top-level composite. */
	private Composite topComposite;

	private Composite dynamicComposite;

	/**
	 * The user id (either as retrieved from the previous page or as freshly
	 * accepted from the server).
	 */
	protected String id = "";

	/** Whether the registration was successful. */
	protected boolean successfulRegistration = false;

	/** The string to be shown as the window title. */
	protected String windowTitle;

	/** The message title to be shown above the messageBody. */
	protected String messageTitle;

	/** The message to be displayed. */
	protected String messageBody;

	/** Constructor. */
	protected RegistrationEndingPage() {
		super("ID created.");
	}

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("", topComposite);
		setControl(topComposite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (dynamicComposite != null) {
				dynamicComposite.dispose();
			}
			makeRegistration();
			createPageContent();
			dynamicComposite.layout(true);
			topComposite.layout(true);
		}
	}

	/** Template method that performs the registration. */
	abstract protected void makeRegistration();

	private void createPageContent() {
		dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
		dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());

		UIUtils.createBoldLabel(messageTitle, dynamicComposite);
		setTitle(windowTitle);
		UIUtils.createLabel(messageBody, dynamicComposite);
	}

	@Override
	public boolean canFinish() {
		return successfulRegistration;
	}

}
