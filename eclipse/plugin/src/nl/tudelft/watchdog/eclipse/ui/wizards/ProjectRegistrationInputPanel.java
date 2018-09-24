package nl.tudelft.watchdog.eclipse.ui.wizards;

import static nl.tudelft.watchdog.core.ui.wizards.Project.BUG_FINDING_USAGE_LABEL_TEXT;
import static nl.tudelft.watchdog.core.ui.wizards.Project.CI_USAGE_LABEL_TEXT;
import static nl.tudelft.watchdog.core.ui.wizards.Project.CODE_STYLE_USAGE_LABEL_TEXT;
import static nl.tudelft.watchdog.core.ui.wizards.Project.CREATE_PROJECT_BUTTON_TEXT;
import static nl.tudelft.watchdog.core.ui.wizards.Project.DO_YOU_USE_STATIC_ANALYSIS;
import static nl.tudelft.watchdog.core.ui.wizards.Project.OTHER_AUTOMATION_USAGE_LABEL_TEXT;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_CREATION_MESSAGE_FAILURE;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_CREATION_MESSAGE_SUCCESSFUL;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_DATA_REQUEST;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_NAME_LABEL;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_NAME_TEXTFIELD_TOOLTIP;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_WEBSITE_LABEL;
import static nl.tudelft.watchdog.core.ui.wizards.Project.PROJECT_WEBSITE_TEXTFIELD_TOOLTIP;
import static nl.tudelft.watchdog.core.ui.wizards.Project.TOOL_USAGE_LABEL_TEXT;
import static nl.tudelft.watchdog.core.ui.wizards.Project.TOOL_USAGE_TEXTFIELD_TOOLTIP;
import static nl.tudelft.watchdog.core.ui.wizards.Project.WATCHDOG_PROJECT_PROFILE;
import static nl.tudelft.watchdog.core.ui.wizards.Project.YOUR_PROJECT_ID_LABEL;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.INPUT_IS_REQUIRED;
import static nl.tudelft.watchdog.eclipse.ui.util.UIUtils.HEADER_FONT;

import java.util.function.Consumer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.logic.network.ServerCommunicationException;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.eclipse.ui.preferences.Preferences;
import nl.tudelft.watchdog.eclipse.ui.util.UIUtils;
import nl.tudelft.watchdog.eclipse.ui.wizards.RegistrationStep.YesNoDontknowButtonGroup;
import nl.tudelft.watchdog.eclipse.util.WatchDogUtils;

class ProjectRegistrationInputPanel extends RegistrationInputPanel {

	private Text projectName;
	private Text projectWebsite;
	private YesNoDontknowButtonGroup ciUsage;
	private YesNoDontknowButtonGroup codeStyleUsage;
	private YesNoDontknowButtonGroup bugFindingUsage;
	private YesNoDontknowButtonGroup automationUsage;
	private Text toolsUsed;
	protected Scale percentageProductionSlider;
	private boolean sliderTouched = false;

	/**
	 * A panel to ask the user questions regarding their project.
	 *
	 * @param container
	 *            The parent container.
	 * @param callback
	 *            The callback invoked after the user clicked "Create WatchDog
	 *            Project".
	 */
	ProjectRegistrationInputPanel(Composite container, Consumer<Boolean> callback) {
		super(container, callback);
		this.setLayout(RegistrationStep.createRowLayout(SWT.VERTICAL));

		Label header = new Label(this, SWT.NONE);
		header.setText(WATCHDOG_PROJECT_PROFILE);
		header.setFont(HEADER_FONT);

		new Label(this, SWT.NONE).setText(PROJECT_DATA_REQUEST);
		new Label(this, SWT.NONE).setText(INPUT_IS_REQUIRED);

		GridLayout gridLayout = new GridLayout(2, true);
		Composite inputFieldContainer = new Composite(this, SWT.NONE);
		inputFieldContainer.setLayout(gridLayout);
		this.createInputFields(inputFieldContainer);

		Composite sliderContainer = new Composite(this, SWT.NONE);
		sliderContainer.setLayout(new GridLayout(1, true));
		createSlider(sliderContainer);

		this.inputContainer = new Composite(this, SWT.NONE);
		this.inputContainer.setLayout(gridLayout);
		this.createButtonAndStatusContainer(CREATE_PROJECT_BUTTON_TEXT);
	}

	private void createInputFields(Composite inputContainer) {
		this.projectName = RegistrationStep.createLinkedLabelTextField(PROJECT_NAME_LABEL,
				PROJECT_NAME_TEXTFIELD_TOOLTIP, inputContainer);
		this.projectWebsite = RegistrationStep.createLinkedLabelTextField(PROJECT_WEBSITE_LABEL,
				PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, inputContainer);
		this.ciUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

		new Label(inputContainer, SWT.NONE).setText(DO_YOU_USE_STATIC_ANALYSIS);
		// Filler to complete this grid row
		new Composite(inputContainer, SWT.NONE);

		this.codeStyleUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(CODE_STYLE_USAGE_LABEL_TEXT,
				inputContainer);
		this.bugFindingUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(BUG_FINDING_USAGE_LABEL_TEXT,
				inputContainer);
		this.automationUsage = RegistrationStep.createYesNoDontKnowQuestionWithLabel(OTHER_AUTOMATION_USAGE_LABEL_TEXT,
				inputContainer);

		this.toolsUsed = RegistrationStep.createLinkedLabelTextField(TOOL_USAGE_LABEL_TEXT,
				TOOL_USAGE_TEXTFIELD_TOOLTIP, inputContainer);
	}

	private void createSlider(Composite composite) {
		UIUtils.createLabel("",	composite);
		UIUtils.createLabel(
				"Estimate how you divide your time into the two activities testing and production. Just have a wild guess!\n",
				composite);

		Composite row = UIUtils.createFullGridedComposite(composite, 3);
		Label testingLabel = UIUtils.createLabel("100% Testing  ", row);
		testingLabel.setToolTipText(
				"To the testing activity, everything you do with Junit tests counts. Examples: writing, modifying, debugging, and executing Junit tests");
		percentageProductionSlider = new Scale(row, SWT.HORIZONTAL);
		percentageProductionSlider.setLayoutData(UIUtils.createFullGridUsageData());
		percentageProductionSlider.setSelection(50);
		percentageProductionSlider.setIncrement(5);
		percentageProductionSlider.setPageIncrement(5);
		percentageProductionSlider.setMaximum(100);
		percentageProductionSlider.setMinimum(0);
		Label productionLabel = UIUtils.createLabel("  100% Production", row);
		productionLabel.setToolTipText(
				"To the production activity, every activity that has to do with regular, non-test production code counts.");
		UIUtils.createLabel("", row);
		final Label sliderValueText = UIUtils.createItalicLabel("50% Testing, 50% Production", row);
		sliderValueText.setLayoutData(UIUtils.createFullGridUsageData());
		sliderValueText.setAlignment(SWT.CENTER);
		percentageProductionSlider.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int developmentTimeValue = percentageProductionSlider.getSelection();
				int testingTimeValue = 100 - developmentTimeValue;
				sliderValueText.setText(testingTimeValue + "% Testing, " + developmentTimeValue + "% Production");
				sliderValueText.update();
				sliderTouched = true;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		UIUtils.createLabel(
				"Testing is every activity related to testing (reading, writing, modifying, refactoring and executing JUnit tests).\nProduction is every activity related to regular code (reading, writing, modifying, and refactoring Java classes).\n",
				composite);

	}

	@Override
	boolean registerAction() {
		if (!sliderTouched) {
			MessageDialog.openWarning(getShell(), "Warning", "To proceed, you have to enter how you divide your time between production and test time, by at least touching the slider.");
			return false;
		}

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
