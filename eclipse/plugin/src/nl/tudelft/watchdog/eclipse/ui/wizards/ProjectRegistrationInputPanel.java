package nl.tudelft.watchdog.eclipse.ui.wizards;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationStep.YesNoDontknowButtonGroup;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;
import static nl.tudelft.watchdog.eclipse.ui.util.UIUtils.HEADER_FONT;

class ProjectRegistrationInputPanel extends RegistrationInputPanel {

	private Text projectName;
	private Text projectWebsite;
	private YesNoDontknowButtonGroup ciUsage;
	private YesNoDontknowButtonGroup codeStyleUsage;
	private YesNoDontknowButtonGroup bugFindingUsage;
	private YesNoDontknowButtonGroup automationUsage;
	private Text toolsUsed;

	/**
	 * A panel to ask the user questions regarding their project.
	 * @param container The parent container.
	 * @param callback The callback invoked after the user clicked "Create WatchDog Project".
	 */
	ProjectRegistrationInputPanel(Composite container, Consumer<Boolean> callback) {
		super(container, callback);
		this.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		Label header = new Label(this, SWT.NONE);
		header.setText(WATCHDOG_PROJECT_PROFILE);
		header.setFont(HEADER_FONT);

		new Label(this, SWT.NONE).setText(PROJECT_DATA_REQUEST);
		new Label(this, SWT.NONE).setText(INPUT_IS_OPTIONAL);

		this.inputContainer = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, true);
		this.inputContainer.setLayout(gridLayout);

		this.createInputFields();

		this.createButtonAndStatusContainer(CREATE_PROJECT_BUTTON_TEXT);
	}

	private void createInputFields() {
		this.projectName = RegistrationStep.createLinkedLabelTextField(PROJECT_NAME_LABEL, PROJECT_NAME_TEXTFIELD_TOOLTIP, inputContainer);
		this.projectWebsite = RegistrationStep.createLinkedLabelTextField(PROJECT_WEBSITE_LABEL, PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, inputContainer);
		this.ciUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

		new Label(inputContainer, SWT.NONE).setText(DO_YOU_USE_STATIC_ANALYSIS);
		// Filler to complete this grid row
		new Composite(inputContainer, SWT.NONE);

		this.codeStyleUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(CODE_STYLE_USAGE_LABEL_TEXT, inputContainer);
		this.bugFindingUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(BUG_FINDING_USAGE_LABEL_TEXT, inputContainer);
		this.automationUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(OTHER_AUTOMATION_USAGE_LABEL_TEXT, inputContainer);

		this.toolsUsed = RegistrationStep.createLinkedLabelTextField(TOOL_USAGE_LABEL_TEXT, TOOL_USAGE_TEXTFIELD_TOOLTIP, inputContainer);
	}

	@Override
	boolean registerAction() {
		Project project = new Project(Preferences.getInstance().getUserId());

		project.name = projectName.getText();
		project.website = projectWebsite.getText();
		project.usesContinuousIntegration = this.ciUsage.selected;
		project.usesCodeStyleSA = this.codeStyleUsage.selected;
		project.usesBugFindingSA = this.bugFindingUsage.selected;
		project.usesOtherAutomationSA = this.automationUsage.selected;
		project.usesToolsSA = this.toolsUsed.getText();

		String projectId;

		try {
			projectId = new JsonTransferer().registerNewProject(project);
		} catch (ServerCommunicationException exception) {
			this.createFailureMessage(PROJECT_CREATION_MESSAGE_FAILURE, exception);

			return false;
		}

		Preferences preferences = Preferences.getInstance();
		preferences.registerProjectId(WatchDogUtils.getWorkspaceName(), projectId);
		preferences.registerProjectUse(WatchDogUtils.getWorkspaceName(), true);

		this.createSuccessIdOutput(PROJECT_CREATION_MESSAGE_SUCCESSFUL, YOUR_PROJECT_ID_LABEL, projectId);

		return true;
	}

}
