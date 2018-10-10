package nl.tudelft.watchdog.intellij.ui.wizards;

import com.intellij.ide.wizard.Step;
import com.intellij.openapi.util.IconLoader;
import nl.tudelft.watchdog.core.ui.wizards.YesNoDontKnowChoice;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static nl.tudelft.watchdog.core.ui.wizards.WizardStrings.*;

public abstract class WizardStep implements Step {

    static final int DEFAULT_SPACING = 5;

    private JComponent panel;

    @Override
    public void _init() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        this._initWithPanel(panel);
    }

    @Override
    public void _commit(boolean b) {

    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public JComponent getComponent() {
        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @NotNull
    JLabel createLogo(String iconPath) {
        JLabel tudelftLogo = new JLabel();
        tudelftLogo.setIcon(IconLoader.getIcon(iconPath));
        tudelftLogo.setHorizontalAlignment(JLabel.CENTER);
        return tudelftLogo;
    }

    abstract void _initWithPanel(Container panel);

    abstract boolean isFinishedWithStep();

    /**
     * Create a label that is linked to the text field. Clicking on the label
     * will select the text field.
     * @param labelText The text of the label.
     * @param tooltip The tooltip for both elements.
     * @param textFieldLength The length of the text field.
     * @param container The parent container.
     * @return The created text field.
     */
    static JTextField createLinkedLabelTextField(String labelText, String tooltip, int textFieldLength, Container container) {
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

    static JComponent createErrorMessageLabel(Exception exception) {
		String exceptionMessage = exception.getMessage();
		if (exceptionMessage != null) {
			int truncateThreshold = Integer.min(exceptionMessage.length(), 100);
			exceptionMessage = exceptionMessage.substring(0, truncateThreshold);
		}
		else {
			exceptionMessage = "No error message transferred.";
		}

		return new JLabel("<html>" +
                 exceptionMessage +
                "<br>" + CONNECTED_TO_INTERNET +
                "<br>" + PLEASE_CONTACT_US + Links.OUR_WEBSITE.toHTMLURL() + ". " + HELP_TROUBLESHOOT, JLabel.LEFT);
    }

    static ButtonGroup createYesNoDontKnowQuestionWithLabel(String labelText, Container container) {
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

    static YesNoDontKnowChoice getChoiceFromButtonGroup(ButtonGroup buttons) {
        final ButtonModel selection = buttons.getSelection();

        if (selection == null) {
            return YesNoDontKnowChoice.DontKnow;
        }

        return YesNoDontKnowChoice.valueOf(selection.getActionCommand());
    }
}
