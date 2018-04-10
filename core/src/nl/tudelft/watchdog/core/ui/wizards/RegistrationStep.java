package nl.tudelft.watchdog.core.ui.wizards;

import nl.tudelft.watchdog.core.ui.wizards.RegistrationWizard;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class RegistrationStep {

    private final RegistrationWizard wizard;

    private Container panel;

    private JPanel dynamicContent;
    private boolean hasValidUserId = false;

    public RegistrationStep(RegistrationWizard wizard) {
        this.wizard = wizard;
    }

    public void _initWithPanel(Container panel) {
        this.panel = panel;

        panel.add(createUserRegistrationIntroduction());
        panel.add(createUserIsRegisteredQuestion());
    }

    public boolean isFinishedWithStep() {
        return hasValidUserId;
    }

    private Component createUserRegistrationIntroduction() {
        Container container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));

        container.add(new JLabel(this.obtainHeaderText()));

        return container;
    }

    protected abstract String obtainHeaderText();

    private Component createUserIsRegisteredQuestion() {
        Container container = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));

        container.add(new JLabel("Do you have a WatchDog registration?"));

        ButtonGroup buttons = new ButtonGroup();

        JRadioButton yes = new JRadioButton("Yes");
        container.add(yes);
        buttons.add(yes);
        whenSelectedCreatePanelAndUpdateUI(yes, getIdInputPanel());

        JRadioButton no = new JRadioButton("No");
        container.add(no);
        buttons.add(no);
        whenSelectedCreatePanelAndUpdateUI(no, getRegistrationPanel());

        this.dynamicContent = new JPanel();
        this.panel.add(this.dynamicContent);

        return container;
    }

    protected abstract Function<Consumer<Boolean>, JPanel> getIdInputPanel();

    protected abstract Function<Consumer<Boolean>,JPanel> getRegistrationPanel();

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
