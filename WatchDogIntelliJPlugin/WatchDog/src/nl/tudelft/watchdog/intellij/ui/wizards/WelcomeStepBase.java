package nl.tudelft.watchdog.intellij.ui.wizards;

import java.awt.event.*;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.DocumentAdapter;
import nl.tudelft.watchdog.intellij.ui.util.UIUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * The first page of a Wizard. It asks an initial yes-or no question. Depending
 * on the answer, it dynamically display an input field or an introduction page.
 */
public abstract class WelcomeStepBase extends WizardStep {

    /**
     * The welcome title. To be changed by subclasses.
     */
    protected String welcomeDisplay;

    /**
     * The text to welcome the user. To be changed by subclasses.
     */
    protected String welcomeText;

    /**
     * The text on the label for the user input. To be changed by subclasses.
     */
    protected String labelText;

    /**
     * The tooltip on the label and inputText.
     */
    protected String inputToolTip;

    /**
     * The label question. To be changed by subclasses.
     */
    protected String labelQuestion;

    /**
     * Whether it is User or Project registration.
     */
    protected String currentRegistration;

    /**
     * The length (in characters) of the WatchDog id.
     */
    private static final int ID_LENGTH = 40;

    /**
     * The JPanel which holds the text field for the new user welcome or
     * holds the text field for the existing user login.
     */
    private JPanel dynamicContent;

    /**
     * The id as entered by the user (note: as delivered from this wizard page,
     * still unchecked).
     */
    private JTextField userInput;

    /**
     * The yes button from the question.
     */
    private JRadioButton radioButtonYes;

    /**
     * Constructor.
     */
    public WelcomeStepBase(String title, int stepNumber, RegistrationWizardBase wizard) {
        super(title, stepNumber, wizard);
    }

    /**
     * Creates and returns the question whether WatchDog Id is already known.
     */
    protected JPanel createQuestionJPanel(final JPanel parent) {
        final JPanel panel = UIUtils.createFlowJPanelLeft(parent);

        JLabel questionIcon = UIUtils.createLabel(panel, "");
        questionIcon.setIcon(IconLoader.getIcon(getIconPath()));

        UIUtils.createBoldLabel(panel, "   " + labelQuestion);

        radioButtonYes = UIUtils.createRadioButton(panel, "Yes");
        final JRadioButton radioButtonNo = UIUtils.createRadioButton(panel,
                "No, I have a " + currentRegistration + "-ID");

        ButtonGroup group = new ButtonGroup();
        group.add(radioButtonNo);
        group.add(radioButtonYes);
        radioButtonYes.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {

                    setErrorMessageAndStepComplete(null);
                    removeDynamicContent(parent);
                    dynamicContent = createWelcomeJPanel(parent);
                    setComplete(true);
                    getWizard().updateButtons();
                    parent.updateUI();
                }
            }
        });

        radioButtonNo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    removeDynamicContent(parent);
                    dynamicContent = createLoginJPanel(parent);
                    setComplete(false);
                    getWizard().updateButtons();
                    parent.updateUI();
                }
            }
        });
        return panel;
    }

    /**
     * @return The path to the image that is displayed next to the question.
     */
    protected abstract String getIconPath();

    /**
     * Removes the dynamic content from the page, if it exists.
     */
    private void removeDynamicContent(final JPanel parent) {
        if (dynamicContent != null) {
            parent.remove(dynamicContent);
            parent.revalidate();
            parent.repaint();
        }
    }

    /**
     * Creates and returns an input field, in which user can enter their
     * existing WatchDog ID.
     */
    private JPanel createLoginJPanel(final JPanel parent) {
        JPanel panel = UIUtils.createFlowJPanelLeft(parent);
        userInput = UIUtils.createLinkedFieldInput(panel, labelText, ID_LENGTH, inputToolTip);
        userInput.setDocument(new UIUtils.JTextFieldLimit(ID_LENGTH));
        userInput.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                if (userInput.getText().length() == ID_LENGTH) {
                    setErrorMessageAndStepComplete(null);
                } else {
                    setErrorMessageAndStepComplete("Not a valid id.");
                }
                parent.updateUI();
                getWizard().updateButtons();
            }
        });

        return panel;
    }

    /**
     * Creates and returns a welcoming panel for new ids.
     */
    private JPanel createWelcomeJPanel(JPanel parent) {
        JPanel panel = UIUtils.createGridedJPanel(parent, 1);
        UIUtils.createLabel(panel,"");
        UIUtils.createBoldLabel(panel, welcomeDisplay);
        return panel;
    }

    /**
     * @return Whether a possibly valid id has been entered.
     */
    public boolean hasValidUserId() {
        return userInput != null && isComplete();
    }

    /**
     * @return The id entered by the user.
     */
    public String getId() {
        return userInput.getText();
    }

    /**
     * @return Whether the user wants to create a new id (<code>true</code> in
     * that case, <code>false</code> otherwise).
     */
    public boolean getRegisterNewId() {
        return radioButtonYes.isSelected();
    }

    @Override
    public boolean canFinish() {
        return true;
    }

}