package nl.tudelft.watchdog.eclipse.ui.wizards.projectregistration;

import org.eclipse.swt.widgets.Composite;

import nl.tudelft.watchdog.core.util.WatchDogGlobals;
import nl.tudelft.watchdog.eclipse.ui.handlers.StartupUIThread;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;
import nl.tudelft.watchdog.eclipse.ui.wizards.FinishableWizardPage;
import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationEndingPageBase;

/**
 * Possible finishing page in the wizard. If the project exists on the server,
 * or the server is not reachable, the user can exit here.
 */
public class ProjectCreatedEndingPage extends RegistrationEndingPageBase {

	/** The top-level composite. */
	private Composite topComposite;

	private Composite dynamicComposite;

	private String concludingMessage;

	/** Constructor. */
	public ProjectCreatedEndingPage(int pageNumber) {
		super("Project-ID created.", pageNumber);
		concludingMessage = ProjectIdEnteredEndingPage.ENCOURAGING_END_MESSAGE;
	}

	@Override
	protected void makeRegistration() {
		windowTitle = "Registration Summary";
		messageTitle = "";
		if (StartupUIThread.makeSilentRegistration()) {
			successfulRegistration = true;
			messageBody = "New user and project successfully registered!";
		} else {
			successfulRegistration = false;
			messageBody = "Registration failed! Do you have an internet connection?";
		}
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

	private void createPageContent() {
		setTitle(windowTitle);
		dynamicComposite = UIUtils.createGridedComposite(topComposite, 1);
		dynamicComposite.setLayoutData(UIUtils.createFullGridUsageData());
		createUserAndProjectRegistrationSummary();
		createDebugSurveyInfo();
		if (successfulRegistration) {
			UIUtils.createLabel(concludingMessage, dynamicComposite);
		}
		return;
	}

	/**
	 * Shows the label and link to ask the new user to fill out the survey on
	 * debugging.
	 */
	private void createDebugSurveyInfo() {
		UIUtils.createBoldLabel(WatchDogGlobals.DEBUG_SURVEY_TEXT,
				dynamicComposite);
		UIUtils.createStartDebugSurveyLink(dynamicComposite);
	}

	private void createUserAndProjectRegistrationSummary() {
		if (successfulRegistration) {
			FinishableWizardPage.createSuccessMessage(dynamicComposite,
					messageTitle, messageBody, null);
		} else {
			FinishableWizardPage.createFailureMessage(dynamicComposite,
					messageTitle, messageBody);
			setPageComplete(false);
		}
	}

	@Override
	public void createControl(Composite parent) {
		topComposite = UIUtils.createGridedComposite(parent, 1);
		UIUtils.createLabel("", topComposite);
		setControl(topComposite);
	}
}
