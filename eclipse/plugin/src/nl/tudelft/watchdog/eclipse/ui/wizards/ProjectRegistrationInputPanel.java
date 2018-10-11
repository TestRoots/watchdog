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

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
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
	private int productionPercentageStart;

	private boolean sliderTouched = false;

	private Composite sliderComposite;
	private Composite sliderRow;

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

		sliderComposite = new Composite(this, SWT.NONE);
		sliderComposite.setLayout(new GridLayout(1, true));
		createSlider(sliderComposite);

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
		UIUtils.createLabel("", composite);
		UIUtils.createLabel(Project.SLIDER_QUESTION, composite);

		this.productionPercentageStart = ThreadLocalRandom.current().nextInt(0, 100 + 1);
		this.sliderRow = UIUtils.createFullGridedComposite(composite, 3);

		Label testingLabel = UIUtils.createLabel("100% Testing  ", sliderRow);
		testingLabel.setToolTipText(Project.SLIDER_TOOLTIP_TESTING);
		this.percentageProductionSlider = new Scale(sliderRow, SWT.HORIZONTAL);
		this.percentageProductionSlider.setLayoutData(UIUtils.createFullGridUsageData());
		this.percentageProductionSlider.setSelection(this.productionPercentageStart);
		this.percentageProductionSlider.setIncrement(5);
		this.percentageProductionSlider.setPageIncrement(5);
		this.percentageProductionSlider.setMaximum(100);
		this.percentageProductionSlider.setMinimum(0);
		Label productionLabel = UIUtils.createLabel("  100% Production", sliderRow);
		productionLabel.setToolTipText(Project.SLIDER_TOOLTIP_PRODUCTION);
		UIUtils.createLabel("", sliderRow);
		final Label sliderValueText = UIUtils.createItalicLabel("", sliderRow);
		sliderValueText.setLayoutData(UIUtils.createFullGridUsageData());
		sliderValueText.setAlignment(SWT.CENTER);

		SelectionListener sliderMovedListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Color backgroundColor = sliderComposite.getParent().getBackground();
				sliderComposite.setBackground(backgroundColor);
				sliderRow.setBackground(backgroundColor);
				int developmentTimeValue = percentageProductionSlider.getSelection();
				int testingTimeValue = 100 - developmentTimeValue;
				sliderValueText.setText(testingTimeValue + "% Testing, " + developmentTimeValue + "% Production");
				sliderValueText.update();
				sliderTouched = true;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		this.percentageProductionSlider.addSelectionListener(sliderMovedListener);
		sliderMovedListener.widgetSelected(null);
		this.sliderTouched = false;

		UIUtils.createLabel(Project.SLIDER_TESTING_DEFINITION, composite);
	}

	@Override
	boolean registerAction() {
		if (!sliderTouched) {
			// Show warning and add background color to slider to make it stand out
			MessageDialog.openWarning(getShell(), "Warning", Project.SLIDER_WARNING);
			Color warningColor = new Color(getDisplay(), new RGB(255, 192, 178));
			this.sliderComposite.setBackground(warningColor);
			this.sliderRow.setBackground(warningColor);

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
		project.productionPercentage = this.percentageProductionSlider.getSelection();
		project.productionPercentageStart = this.productionPercentageStart;

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
