package nl.tudelft.watchdog.ui.wizards;

import com.intellij.ide.wizard.CommitStepException;
import com.intellij.ide.wizard.Step;
import nl.tudelft.watchdog.ui.util.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * A Wizard {@link Step} that can determine for itself via the
 * {@link #canFinish()} method, whether the wizard should be completeable via
 * the finish button.
 */
public abstract class WizardStep implements Step {

    protected enum CommitType {
        Prev, Next, Finish
    }

    /** Main panel of the step. */
    protected JPanel topPanel;

	/** ID of the current step. */
	protected final int stepID;

    protected Icon myIcon;

    private boolean isComplete = true;

    protected String myTitle;

    protected String descriptionText;

    private final RegistrationWizardBase myWizard;

    /** Constructor. */
	protected WizardStep(String stepName, int stepID, RegistrationWizardBase wizard) {
        this.myTitle = stepName;
        this.stepID = stepID;
        this.myWizard = wizard;
	}

    /** Getting the current wizard.  */
    public final RegistrationWizardBase getWizard() {
        return myWizard;
    }

    /** Getting title of this step.*/
    public String getTitle() {
        return myTitle;
    }

    /** Setting title of this step.*/
    public void setTitle(String title) {
        this.myTitle = title;
    }

    /** Called on hitting Previous button. */
    public final void _commitPrev() throws CommitStepException {
        commit(CommitType.Prev);
    }

    /** Determines whether the pressed button was "Next" or "Finish". */
    public final void _commit(boolean finishChosen) throws CommitStepException {
        commit(finishChosen ? CommitType.Finish : CommitType.Next);
    }

    /** Each step should implement this to "commit" its internal data */
    protected abstract void commit(CommitType commitType);

    @Override
    public JComponent getComponent() {
        return topPanel;
    }

	/** @return whether this step can currently be finished. */
	public abstract boolean canFinish();

    /**
     * @return Whether a step is complete or not.
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Sets whether a step is complete.
     */
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public int getStepId() {
        return stepID;
    }

    /**
	 * Calls {@link #setErrorMessage(String)} with the supplied errorMessage,
	 * and {@link #setComplete(boolean)} true if the message is empty, and
	 * false otherwise.
	 */
	protected void setErrorMessageAndStepComplete(String errorMessage) {
		setErrorMessage(errorMessage);
		setComplete(errorMessage == null);
	}

    protected void setErrorMessage(String errorMessage) {
        getWizard().updateButtons();
        //TODO Add error message to the panel
    }


	/**
	 * Validates the form inputs, and sets the error message for the wizard if
	 * there is any. Empty default implementation can be overridden by
	 * subclasses.
	 */
	public void validateFormInputs() {
	}

	/**
	 * Creates a simple question with according yes/no/don't know radio buttons.
	 * 
	 * @return the panel where the buttons are put onto.
	 */
	protected JPanel createSimpleYesNoDontKnowQuestion(JPanel parent) {
        JPanel buttons = createSimpleYesNoQuestion(parent);
        ButtonGroup group = new ButtonGroup();
		JRadioButton button = UIUtils.createRadioButton(buttons, "Don't know");
        group.add((JRadioButton) buttons.getComponent(0));
        group.add((JRadioButton) buttons.getComponent(1));
        group.add(button);
		return buttons;
	}

	/**
	 * Creates a simple question with according yes/no radio buttons.
	 * 
	 * @return the panel where the buttons are put onto.
	 */
	protected JPanel createSimpleYesNoQuestion(JPanel parent) {
		JPanel panel = UIUtils.createFlowJPanelLeft(parent);
        ButtonGroup group = new ButtonGroup();
		JRadioButton button = UIUtils.createRadioButton(panel, "Yes");
		group.add(button);
        button = UIUtils.createRadioButton(panel, "No");
        group.add(button);
		return panel;
	}

	/** Use on panels which consist of radio buttons only. */
	protected void addValidationListenerToAllChildren(JPanel panel,
			FormValidationListener listener) {
		for (Component component : panel.getComponents()) {
            JRadioButton button = (JRadioButton) component;
			button.addItemListener(listener);
		}
	}

	/**
	 * Checks for the given panel if of all its children, which are assumed
	 * to be radio buttons, at least one has been selected. Use on panels which
	 * consist of radio buttons only.
	 */
	protected boolean hasOneSelection(JPanel panel) {
		boolean oneSelected = false;
		for (Component component : panel.getComponents()) {
            JRadioButton button = (JRadioButton) component;
			oneSelected = oneSelected ^ button.isSelected();
		}
		return oneSelected;
	}

    /** Creates a new JPanel with the TestRoots and WatchDog logo. */
	protected void createLogoRow(JPanel parent) {
		JPanel logoContainer = UIUtils.createGridedJPanel(parent, 2);
        JPanel leftLogo = UIUtils.createFlowJPanelCenter(logoContainer);
		UIUtils.createWatchDogLogo(leftLogo);
        JPanel rightLogo = UIUtils.createFlowJPanelCenter(logoContainer);
		UIUtils.createLogo(rightLogo,"/images/testroots_small.png");
	}

	/**
	 * Given a panel consisting of three buttons Yes/No/Don't Know, returns
	 * which of the buttons was clicked.
	 */
	public YesNoDontKnowChoice evaluateWhichSelection(JPanel yesNoDontKnowJPanel) {
		if (((JRadioButton) yesNoDontKnowJPanel.getComponents()[0]).isSelected()) {
			return YesNoDontKnowChoice.Yes;
		} else if (((JRadioButton) yesNoDontKnowJPanel.getComponents()[1]).isSelected()) {
			return YesNoDontKnowChoice.No;
		}
		return YesNoDontKnowChoice.DontKnow;
	}

	/** Creates message on failure. */
	public static void createFailureMessage(JPanel parent, String title,
			String message) {
		UIUtils.createBoldLabel(parent, title);
        JPanel innerParent = UIUtils.createFlowJPanelLeft(parent);
		UIUtils.createLogo(innerParent, "/images/errormark.png");
		UIUtils.createLabel(innerParent, message);
	}

	/** Creates message on success. */
	public static void createSuccessMessage(JPanel parent, String title,
			String message, String id) {
		UIUtils.createBoldLabel(parent, title);
		JPanel innerParent = UIUtils.createFlowJPanelLeft(parent);
		UIUtils.createLogo(innerParent, "/images/checkmark.png");
		JPanel displayInformation = UIUtils.createFlowJPanelLeft(innerParent);
		UIUtils.createLabel(displayInformation, message);
		UIUtils.createTextField(displayInformation, id);
	}

    protected void createHeader(JComponent parent) {
        JPanel headerPanel = UIUtils.createGridedJPanel(parent, 2);
        JPanel leftPanel = UIUtils.createGridedJPanel(headerPanel, 1);
        UIUtils.createBoldLabel(leftPanel, getTitle());
        UIUtils.createLabel(leftPanel, descriptionText);

        JPanel rightPanel = UIUtils.createFlowJPanelRight(headerPanel);
        UIUtils.createTUDLogo(rightPanel);
    }

    /** Refreshes the UI and updates the buttons.  */
    protected void updateStep() {
        topPanel.updateUI();
        getWizard().updateButtons();
    }

    @Override
    public void _init() {
        topPanel = new JPanel();
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }
}
