package nl.tudelft.watchdog.intellij.ui.wizards.projectregistration;

import nl.tudelft.watchdog.intellij.ui.util.UIUtils;
import nl.tudelft.watchdog.intellij.ui.wizards.FormValidationListener;
import nl.tudelft.watchdog.intellij.ui.wizards.RegistrationWizardBase;
import nl.tudelft.watchdog.intellij.ui.wizards.WizardStep;
import nl.tudelft.watchdog.core.ui.wizards.YesNoDontKnowChoice;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A Page displaying a slider so that the user can estimate how his or her time
 * is distributed between production code and testing.
 */
public class ProjectSliderStep extends WizardStep {

    private JPanel junitForUnitTestingOnly;
    private JPanel testDrivenDesing;
    private boolean sliderTouched = false;

    /**
     * The slider. Its value denotes in full percentage how much production code
     * the user estimates to write.
     */
    protected JSlider percentageProductionSlider;

    /**
     * Constructor.
     */
    public ProjectSliderStep(int pageNumber, RegistrationWizardBase wizard) {
        super("Time Distrubtion", pageNumber, wizard);
        setTitle("Register a new project");
        descriptionText = "You nearly made it! Only this page left.";
    }

    public void createContent(JPanel parent) {

        JPanel questionPanel = UIUtils.createFlowJPanelLeft(parent);
        UIUtils.createLabel(questionPanel,
                "Estimate how you divide your time into the two activities testing and production. Just have a wild guess!");

        JPanel sliderPanel = UIUtils.createFlowJPanelCenter(parent);
        JPanel row = UIUtils.createFlowJPanelLeft(sliderPanel);

        JLabel testingLabel = UIUtils.createLabel(row, "100% Testing  ");
        testingLabel
                .setToolTipText("To the testing activity, everything you do with Junit tests counts. Examples: writing, modifying, debugging, and executing Junit tests");

        percentageProductionSlider = new JSlider(JSlider.HORIZONTAL);
        percentageProductionSlider.setValue(50);
        percentageProductionSlider.setMinorTickSpacing(5);
        percentageProductionSlider.setSnapToTicks(false);
        percentageProductionSlider.setMaximum(100);
        percentageProductionSlider.setMinimum(0);
        percentageProductionSlider.setSize(questionPanel.getPreferredSize());
        row.add(percentageProductionSlider);

        JLabel productionLabel = UIUtils.createLabel(row, "  100% Production");
        productionLabel
                .setToolTipText("To the production activity, every activity that has to do with regular, non-test production code counts.");

        JPanel sliderValuePanel = UIUtils.createFlowJPanelCenter(parent);
        final JLabel sliderValueText = UIUtils.createItalicLabel(
                sliderValuePanel, "50% Testing, 50% Production");
        percentageProductionSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int developmentTimeValue = percentageProductionSlider
                        .getValue();

                int testingTimeValue = 100 - developmentTimeValue;
                sliderValueText.setText(testingTimeValue
                        + "% Testing, " + developmentTimeValue
                        + "% Production");
                sliderValueText.updateUI();
                sliderTouched = true;
                validateFormInputs();
            }
        });

        JPanel descriptionPanel = UIUtils.createGridedJPanel(parent, 1);
        UIUtils.createLabel(
                descriptionPanel,
                "Testing is every activity related to testing (reading, writing, modifying, refactoring and executing JUnit tests).");
        UIUtils.createLabel(
                descriptionPanel,
                "<html>Production is every activity related to regular code (reading, writing, modifying, and refactoring Java classes). <br><br>");

        JPanel questionsPanel = UIUtils.createGridedJPanel(parent,2);
        UIUtils.createLabel(questionsPanel,
                "<html>Do you use JUnit only for unit testing<br>(i.e. only one production class tested per Junit test class)?</html>");

        JPanel answerPanel1 = UIUtils.createFlowJPanelCenter(questionsPanel);
        junitForUnitTestingOnly = createSimpleYesNoDontKnowQuestion(answerPanel1);
        UIUtils.createLabel(questionsPanel, "<html>Do you follow Test-Driven Design or similar practices <br>(Test-first)? ");
        JPanel answerPanel2 = UIUtils.createFlowJPanelCenter(questionsPanel);
        testDrivenDesing = createSimpleYesNoDontKnowQuestion(answerPanel2);
        FormValidationListener formValidationListener = new FormValidationListener(
                this);
        addValidationListenerToAllChildren(junitForUnitTestingOnly,
                formValidationListener);
        addValidationListenerToAllChildren(testDrivenDesing,
                formValidationListener);

    }

    @Override
    public void validateFormInputs() {
        if (!sliderTouched) {
            setErrorMessage("Move the slider to estimate your personal time distribution.");
        } else if (!hasOneSelection(junitForUnitTestingOnly)
                || !hasOneSelection(testDrivenDesing)) {
            setErrorMessageAndStepComplete("Please answer all yes/no/don't know questions!");
        } else {
            setErrorMessageAndStepComplete(null);
        }
        updateStep();
    }

    @Override
    protected void commit(CommitType commitType) {

    }

    @Override
    public boolean canFinish() {
        return false;
    }

    /**
     * @return Whether this project uses Junit for Unit testing only.
     */
    /* package */YesNoDontKnowChoice usesJunitForUnitTestingOnly() {
        return evaluateWhichSelection(junitForUnitTestingOnly);
    }

    /**
     * @return Whether this project uses TDD.
     */
	/* package */YesNoDontKnowChoice usesTestDrivenDesing() {
        return evaluateWhichSelection(testDrivenDesing);
    }

    @Override
    public void _init() {
        super._init();
        JPanel oneColumn = UIUtils.createVerticalBoxJPanel(topPanel);
        createHeader(oneColumn);
        createContent(oneColumn);
        setComplete(false);

    }
}
