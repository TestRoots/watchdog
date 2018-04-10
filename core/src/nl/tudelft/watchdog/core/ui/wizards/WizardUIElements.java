package nl.tudelft.watchdog.core.ui.wizards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WizardUIElements {

    /**
     * The length (in characters) of the WatchDog id.
     */
    public static final int ID_LENGTH = 40;
    public static final int DEFAULT_SPACING = 5;

    public static JTextField createLinkedLabelTextField(String labelText, String tooltip, int textFieldLength, Container container) {
        JLabel label = new JLabel(labelText);
        label.setToolTipText(tooltip);
        container.add(label);

        JTextField textfield = new JTextField(textFieldLength);
        textfield.setToolTipText(tooltip);
        container.add(textfield);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                textfield.grabFocus();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                textfield.grabFocus();
            }
        });
        return textfield;
    }

    public static Component createErrorMessageLabel(Exception exception) {
        return new JLabel("<html>" +
                exception.getMessage() +
                "<br>Are you connected to the internet, and is port 80 open?" +
                "<br>Please contact us via <a href=\"https://www.testroots.org\">our website</a>. We can help troubleshoot the issue!");
    }

    public static ButtonGroup createYesNoDontKnowQuestionWithLabel(String labelText, Container container) {
        JLabel label = new JLabel(labelText);
        container.add(label);

        Container buttonContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        container.add(buttonContainer);

        ButtonGroup buttons = new ButtonGroup();

        JRadioButton yes = new JRadioButton("Yes");
        yes.setActionCommand(YesNoDontKnowChoice.Yes.name());
        buttonContainer.add(yes);
        buttons.add(yes);

        JRadioButton no = new JRadioButton("No");
        no.setActionCommand(YesNoDontKnowChoice.No.name());
        buttonContainer.add(no);
        buttons.add(no);

        JRadioButton dontKnow = new JRadioButton("Don't know");
        dontKnow.setActionCommand(YesNoDontKnowChoice.DontKnow.name());
        buttonContainer.add(dontKnow);
        buttons.add(dontKnow);

        return buttons;
    }

    public static YesNoDontKnowChoice getChoiceFromButtonGroup(ButtonGroup buttons) {
        final ButtonModel selection = buttons.getSelection();

        if (selection == null) {
            return YesNoDontKnowChoice.DontKnow;
        }

        return YesNoDontKnowChoice.valueOf(selection.getActionCommand());
    }
}
