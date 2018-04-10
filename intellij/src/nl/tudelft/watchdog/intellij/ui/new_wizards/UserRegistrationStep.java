package nl.tudelft.watchdog.intellij.ui.new_wizards;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

class UserRegistrationStep extends WizardStep {

    /**
     * The length (in characters) of the WatchDog id.
     */
    static final int ID_LENGTH = 40;

    private final RegistrationWorkflowWizard wizard;

    private Container panel;

    private JPanel dynamicContent;
    private boolean hasValidUserId = false;

    UserRegistrationStep(RegistrationWorkflowWizard wizard) {
        this.wizard = wizard;
    }

    @Override
    void _initWithPanel(Container panel) {
        this.panel = panel;

        panel.add(createUserRegistrationIntroduction());
        panel.add(createUserIsRegisteredQuestion());
    }

    @Override
    boolean isFinishedWithStep() {
        return hasValidUserId;
    }

    private Component createUserRegistrationIntroduction() {
        Container container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));

        container.add(new JLabel("<html>" +
                "<h1>User registration</h1>" +
                "Before we start, we first have to have a WatchDog user registration<br>", JLabel.LEADING));

        return container;
    }

    private Component createUserIsRegisteredQuestion() {
        Container container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));

        container.add(new JLabel("Do you have a WatchDog user registration?", JLabel.LEADING));

        ButtonGroup buttons = new ButtonGroup();

        JRadioButton yes = new JRadioButton("Yes");
        container.add(yes);
        buttons.add(yes);
        whenSelectedCreatePanelAndUpdateUI(yes, UserIdInputPanel::new);

        JRadioButton no = new JRadioButton("No");
        container.add(no);
        buttons.add(no);
        whenSelectedCreatePanelAndUpdateUI(no, UserRegistrationInputPanel::new);

        this.dynamicContent = new JPanel();
        this.panel.add(this.dynamicContent);

        return container;
    }

    private void whenSelectedCreatePanelAndUpdateUI(JRadioButton button, Function<Consumer<Boolean>, JPanel> panelConstructor) {
        button.addItemListener(itemEvent -> {
            if (button.isSelected()) {
                this.panel.remove(this.dynamicContent);

                this.dynamicContent = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
                this.dynamicContent.setLayout(new BoxLayout(this.dynamicContent, BoxLayout.Y_AXIS));

                // Create the panel with the new container and attach a listener for when the
                // panel has finished obtaining all the required information
                this.dynamicContent.add(panelConstructor.apply(hasValidUserId-> {
                    this.hasValidUserId = hasValidUserId;
                    this.dynamicContent.updateUI();
                    wizard.updateButtons();
                }));

                this.panel.add(dynamicContent);

                this.panel.revalidate();
                this.panel.repaint();
            }
        });
    }

}
