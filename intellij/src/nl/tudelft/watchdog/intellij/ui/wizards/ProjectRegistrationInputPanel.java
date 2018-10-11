package nl.tudelft.watchdog.intellij.ui.wizards;

import nl.tudelft.watchdog.core.logic.network.JsonTransferer;
import nl.tudelft.watchdog.core.ui.wizards.Project;
import nl.tudelft.watchdog.intellij.ui.preferences.Preferences;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.intellij.util.WatchDogUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static nl.tudelft.watchdog.core.ui.wizards.Project.*;
import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.INPUT_IS_REQUIRED;

class ProjectRegistrationInputPanel extends RegistrationInputPanel {

    private final JPanel slider;
    /**
     * The slider. Its value denotes in full percentage how much production code
     * the user estimates to write.
     */
    protected JSlider percentageProductionSlider;
    private JTextField projectName;
    private JTextField projectWebsite;
    private ButtonGroup ciUsage;
    private ButtonGroup codeStyleUsage;
    private ButtonGroup bugFindingUsage;
    private ButtonGroup automationUsage;
    private JTextField toolsUsed;
    private Boolean sliderTouched = false;
    private int productionPercentageStart;

    /**
     * A panel to ask the user questions regarding their project.
     *
     * @param callback The callback invoked after the user clicked "Create WatchDog Project".
     */
    ProjectRegistrationInputPanel(Consumer<Boolean> callback) {
        super(callback);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel introductionContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        this.add(introductionContainer);
        introductionContainer.add(new JLabel("<html>" +
            "<h3>" + WATCHDOG_PROJECT_PROFILE + "</h3>" +
            PROJECT_DATA_REQUEST + "<br>" +
            INPUT_IS_REQUIRED));

        JPanel questionContainer = new JPanel(new GridLayout(0, 2));
        this.add(questionContainer);

        this.createInputFields(questionContainer);

        this.add(Box.createVerticalStrut(20));

        this.slider = new JPanel(new GridLayout(0, 1));
        this.createSlider(slider);
        this.add(slider);

        this.inputContainer = new JPanel(new GridLayout(0, 2));
        this.add(inputContainer);

        this.createButtonAndStatusContainer(CREATE_PROJECT_BUTTON_TEXT);
    }

    private void createInputFields(JPanel inputContainer) {
        this.projectName = WizardStep.createLinkedLabelTextField(PROJECT_NAME_LABEL, PROJECT_NAME_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.projectWebsite = WizardStep.createLinkedLabelTextField(PROJECT_WEBSITE_LABEL, PROJECT_WEBSITE_TEXTFIELD_TOOLTIP, 150, inputContainer);
        this.ciUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(CI_USAGE_LABEL_TEXT, inputContainer);

        inputContainer.add(new JLabel(DO_YOU_USE_STATIC_ANALYSIS));
        inputContainer.add(Box.createGlue());

        this.codeStyleUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(CODE_STYLE_USAGE_LABEL_TEXT, inputContainer);
        this.bugFindingUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(BUG_FINDING_USAGE_LABEL_TEXT, inputContainer);
        this.automationUsage = WizardStep.createYesNoDontKnowQuestionWithLabel(OTHER_AUTOMATION_USAGE_LABEL_TEXT, inputContainer);

        this.toolsUsed = WizardStep.createLinkedLabelTextField(TOOL_USAGE_LABEL_TEXT, TOOL_USAGE_TEXTFIELD_TOOLTIP, 150, inputContainer);
    }

    private void createSlider(JPanel parent) {
        JPanel questionPanel = UIUtils.createFlowJPanelLeft(parent);

        UIUtils.createLabel(questionPanel,
            SLIDER_QUESTION);

        this.productionPercentageStart = ThreadLocalRandom.current().nextInt(0, 100 + 1);

        JPanel sliderPanel = UIUtils.createFlowJPanelCenter(parent);
        JPanel row = UIUtils.createFlowJPanelLeft(sliderPanel);

        JLabel testingLabel = UIUtils.createLabel(row, "100% Testing  ");
        testingLabel
            .setToolTipText(SLIDER_TOOLTIP_TESTING);

        percentageProductionSlider = new JSlider(JSlider.HORIZONTAL);
        percentageProductionSlider.setValue(this.productionPercentageStart);
        percentageProductionSlider.setMinorTickSpacing(5);
        percentageProductionSlider.setSnapToTicks(false);
        percentageProductionSlider.setMaximum(100);
        percentageProductionSlider.setMinimum(0);
        percentageProductionSlider.setSize(questionPanel.getPreferredSize());
        row.add(percentageProductionSlider);

        JLabel productionLabel = UIUtils.createLabel(row, "  100% Production");
        productionLabel
            .setToolTipText(SLIDER_TOOLTIP_PRODUCTION);

        JPanel sliderValuePanel = UIUtils.createFlowJPanelCenter(parent);
        JLabel sliderValueText = UIUtils.createItalicLabel(
            sliderValuePanel, "");
        ChangeListener sliderMovedListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    Color backgroundColor = inputContainer.getBackground();
                    colorAllDescendants(slider, backgroundColor);
                } catch (Exception ex) {
                }
                int developmentTimeValue = percentageProductionSlider
                    .getValue();
                int testingTimeValue = 100 - developmentTimeValue;
                sliderValueText.setText(testingTimeValue
                    + "% Testing, " + developmentTimeValue
                    + "% Production");
                sliderValueText.updateUI();
                sliderTouched = true;
            }
        };
        percentageProductionSlider.addChangeListener(sliderMovedListener);
        sliderMovedListener.stateChanged(null);
        this.sliderTouched = false;

        UIUtils.createLabel(
            parent,
            SLIDER_TESTING_DEFINITION);
        UIUtils.createLabel(
            parent,
            SLIDER_PRODUCTION_DEFINITION);
    }

    void colorAllDescendants(JComponent panel, Color color) {
        panel.setBackground(color);

        if (panel.getComponentCount() > 0)
            for (Component component : panel.getComponents()) {
                colorAllDescendants((JComponent) component, color);
            }
    }

    @Override
    boolean registerAction() {
        if (!sliderTouched) {
            // Show warning and add background color to slider to make it stand out
            JOptionPane.showMessageDialog(new JFrame(), SLIDER_WARNING, "Warning",
                JOptionPane.WARNING_MESSAGE);
            Color warningColor = new Color(255, 192, 178);
            colorAllDescendants(slider, warningColor);

            return false;
        }

        Project project = new Project(Preferences.getInstance().getUserId());

        project.name = projectName.getText();
        project.website = projectWebsite.getText();
        project.usesContinuousIntegration = WizardStep.getChoiceFromButtonGroup(ciUsage);
        project.usesCodeStyleSA = WizardStep.getChoiceFromButtonGroup(codeStyleUsage);
        project.usesBugFindingSA = WizardStep.getChoiceFromButtonGroup(bugFindingUsage);
        project.usesOtherAutomationSA = WizardStep.getChoiceFromButtonGroup(automationUsage);
        project.usesToolsSA = toolsUsed.getText();
        project.productionPercentage = this.percentageProductionSlider.getValue();
        project.productionPercentageStart = this.productionPercentageStart;

        String projectId;

        try {
            projectId = new JsonTransferer().registerNewProject(project);
        } catch (Exception exception) {
            this.createFailureMessage(PROJECT_CREATION_MESSAGE_FAILURE, exception);

            return false;
        }

        Preferences preferences = Preferences.getInstance();
        preferences.registerProjectId(WatchDogUtils.getProjectName(), projectId);
        preferences.registerProjectUse(WatchDogUtils.getProjectName(), true);

        this.createSuccessIdOutput(PROJECT_CREATION_MESSAGE_SUCCESSFUL, YOUR_PROJECT_ID_LABEL, projectId);

        return true;
    }
}
